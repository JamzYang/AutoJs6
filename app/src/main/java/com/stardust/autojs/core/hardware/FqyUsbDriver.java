package com.stardust.autojs.core.hardware;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * 风清扬 (FQY/CH591R) 硬件 USB 驱动
 * 
 * 移植指南：
 * 1. 将此文件复制到 Auto.js 源码的 app/src/main/java/com/stardust/autojs/core/hardware/ 目录下。
 * 2. 如果包名不同，请修改第一行的 package 声明。
 */
public class FqyUsbDriver {
    private static final String TAG = "FqyUsbDriver";
    private static final String ACTION_USB_PERMISSION = "com.stardust.autojs.USB_PERMISSION";
    
    // FQY 硬件 (CH591R/CH582) 的 VID (WinChipHead)
    private static final int VENDOR_ID = 17224; // 0x4348
    // PID 可能会变化，通常建议只校验 VID，或者在 connect 时打印 PID 确认
    // private static final int PRODUCT_ID = 21796; 

    private Context mContext;
    private UsbManager mUsbManager;
    private UsbDeviceConnection mConnection;
    private UsbEndpoint mEndpointOut;
    private UsbEndpoint mEndpointIn;
    
    // 是否已连接
    private boolean mIsConnected = false;

    public FqyUsbDriver(Context context) {
        this.mContext = context;
        this.mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
    }

    /**
     * 检查是否已连接
     */
    public boolean isConnected() {
        return mIsConnected;
    }

    /**
     * 自动搜索并连接设备
     * @return 连接成功返回 true
     */
    public boolean connect() {
        if (mIsConnected) {
            return true;
        }

        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        UsbDevice targetDevice = null;

        Log.d(TAG, "Scanning USB devices...");
        for (UsbDevice device : deviceList.values()) {
            Log.d(TAG, "Found device: VID=" + device.getVendorId() + ", PID=" + device.getProductId());
            if (device.getVendorId() == VENDOR_ID) {
                targetDevice = device;
                Log.i(TAG, "Target FQY device found: " + device);
                break;
            }
        }

        if (targetDevice == null) {
            Log.e(TAG, "FQY Device (VID 17224) not found!");
            return false;
        }

        // 检查权限
        if (!mUsbManager.hasPermission(targetDevice)) {
            Log.i(TAG, "Requesting USB permission...");
            PendingIntent permissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE);
            mUsbManager.requestPermission(targetDevice, permissionIntent);
            return false; // 需要等待用户授权，这里先返回 false
        }

        return openDevice(targetDevice);
    }
    
    /**
     * 打开设备并获取端点
     */
    private boolean openDevice(UsbDevice device) {
        UsbInterface usbInterface = null;
        
        // 通常接口 0 是数据接口，或者需要遍历找到 Bulk Transfer 接口
        // 如果是 CDC-ACM，可能有多个 interface (Control + Data)
        // 这里的逻辑是寻找第一个包含 Bulk 端点的接口
        for (int i = 0; i < device.getInterfaceCount(); i++) {
            UsbInterface iface = device.getInterface(i);
            for (int j = 0; j < iface.getEndpointCount(); j++) {
                if (iface.getEndpoint(j).getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                    usbInterface = iface;
                    break;
                }
            }
            if (usbInterface != null) break;
        }

        if (usbInterface == null) {
           // Fallback: Default to interface 0
           usbInterface = device.getInterface(0); 
        }

        UsbDeviceConnection connection = mUsbManager.openDevice(device);
        if (connection == null) {
            Log.e(TAG, "Failed to open USB device connection");
            return false;
        }

        if (!connection.claimInterface(usbInterface, true)) {
            Log.e(TAG, "Failed to claim USB interface");
            connection.close();
            return false;
        }

        mConnection = connection;
        mEndpointOut = null;
        mEndpointIn = null;

        // 获取输入输出端点
        for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
            UsbEndpoint ep = usbInterface.getEndpoint(i);
            if (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {
                    mEndpointOut = ep;
                    Log.d(TAG, "Found Endpoint OUT: " + ep.getAddress());
                } else if (ep.getDirection() == UsbConstants.USB_DIR_IN) {
                    mEndpointIn = ep;
                    Log.d(TAG, "Found Endpoint IN: " + ep.getAddress());
                }
            }
        }
        
        // 对于 CH591 这种模拟串口，可能需要设置波特率
        // 许多时候默认波特率即可工作，如果不行通过 ControlTransfer 设置
        // setBaudRate(115200);

        if (mEndpointOut == null) {
            Log.e(TAG, "Critical: Endpoint OUT not found!");
            close();
            return false;
        }

        mIsConnected = true;
        Log.i(TAG, "FQY Device Connected Successfully!");
        
        // 发送初始化握手 (可选，根据项目分析)
        // sendCmd("$#init:|"); 
        
        return true;
    }

    /**
     * 发送字符串指令
     */
    public synchronized void send(String cmd) {
        if (!mIsConnected || mConnection == null || mEndpointOut == null) {
            Log.w(TAG, "Device not connected, cannot send: " + cmd);
            return;
        }

        byte[] bytes = cmd.getBytes(StandardCharsets.UTF_8);
        int result = mConnection.bulkTransfer(mEndpointOut, bytes, bytes.length, 100);
        
        if (result < 0) {
            Log.e(TAG, "Bulk transfer failed: " + result);
            // 可以在这里处理自动重连逻辑
            mIsConnected = false;
        }
    }
    
    /**
     * 断开连接
     */
    public void close() {
        try {
            if (mConnection != null) {
                mConnection.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error closing connection", e);
        } finally {
            mConnection = null;
            mEndpointOut = null;
            mEndpointIn = null;
            mIsConnected = false;
        }
    }

    // ==========================================
    // 业务功能封装 (与 project_analysis.md 一致)
    // ==========================================

    public void touchDown(int x, int y) {
        // 格式: $#md:x,y|
        send("$#md:" + x + "," + y + "|");
    }

    public void touchMove(int x, int y) {
        // 格式: $#mm:x,y|
        send("$#mm:" + x + "," + y + "|");
    }

    public void touchUp(int x, int y) {
        // 格式: $#mu:x,y|
        send("$#mu:" + x + "," + y + "|");
    }
    
    public void keyPress(int keyCode) {
        // 格式: $#kp:code|
        send("$#kp:" + keyCode + "|");
    }

    public void reset() {
        send("$#init:|");
    }
}
