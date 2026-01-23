# Auto.js 源码集成 FQY 驱动开发指南

本文档描述了如何将风清扬 (FQY/CH591R) 硬件的 USB 驱动直接集成到 Auto.js Android 项目源码中。

## 1. 目标
*   **去除 SO 依赖**：完全脱离 `libnative.so` 和 `libengine.so`。
*   **原生支持**：在 Auto.js 引擎层面直接支持 FQY 硬件，脚本只需简单调用。
*   **即插即用**：利用 `AndroidManifest.xml` 实现 USB 插入自动唤醒或权限预授权。

## 2. 核心修改步骤

### 步骤 A: 添加 Android 权限 (`AndroidManifest.xml`)

在 Auto.js 主模块（通常是 `app/src/main/AndroidManifest.xml`）中添加以下权限和特性声明：

```xml
<manifest ...>
    <!-- 必要的 USB 主机权限 -->
    <uses-feature android:name="android.hardware.usb.host" android:required="true" />
    
    <!-- 用于蓝牙模式 (备用) -->
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
    <uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
    <uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />

    <application ...>
        <!-- 注册 USB 插拔广播 (可选，用于自动启动) -->
        <!--
        <activity ...>
            <intent-filter>
                <action android:name="android.hardware.usb.action.USB_DEVICE_ATTACHED" />
            </intent-filter>
            <meta-data android:name="android.hardware.usb.action.USB_DEVICE_ATTACHED" android:resource="@xml/device_filter" />
        </activity>
        -->
    </application>
</manifest>
```

**提示**：创建一个 `res/xml/device_filter.xml` 文件来指定我们只关心 VID=17224 的设备：
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- VID 17224 (0x4348) WinChipHead (WCH) -->
    <usb-device vendor-id="17224" product-id="21796" /> <!-- PID 可能会变，建议只填 VID 或在代码里判断 -->
</resources>
```

### 步骤 B: 实现 Java 驱动类 (`FqyUsbDriver.java`)

我们需要创建一个单例类来管理 USB连接。

**建议路径**: `app/src/main/java/com/stardust/autojs/core/hardware/FqyUsbDriver.java` (根据实际源码包结构调整)

```java
package com.stardust.autojs.core.hardware;

import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class FqyUsbDriver {
    private static final String TAG = "FqyUsbDriver";
    private static final int VENDOR_ID = 17224; // 0x4348

    private Context mContext;
    private UsbManager mUsbManager;
    private UsbDeviceConnection mConnection;
    private UsbEndpoint mEndpointOut;
    private UsbEndpoint mEndpointIn;

    public FqyUsbDriver(Context context) {
        this.mContext = context;
        this.mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
    }

    /**
     * 尝试连接 FQY 设备
     */
    public boolean connect() {
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        UsbDevice targetDevice = null;
        
        for (UsbDevice device : deviceList.values()) {
            if (device.getVendorId() == VENDOR_ID) {
                targetDevice = device;
                break;
            }
        }

        if (targetDevice == null) {
            Log.e(TAG, "FQY Device not found!");
            return false;
        }

        if (!mUsbManager.hasPermission(targetDevice)) {
            // 在这里处理权限申请，或者如果在 Activity 中已经申请过，这里会返回 true
            Log.e(TAG, "No permission for device: " + targetDevice);
            return false;
        }

        return openDevice(targetDevice);
    }

    private boolean openDevice(UsbDevice device) {
        UsbInterface usbInterface = device.getInterface(0); // 通常是接口 0
        // 如果是 CDC 设备，可能需要遍历接口寻找 Bulk Transfer 的接口
        
        UsbDeviceConnection connection = mUsbManager.openDevice(device);
        if (connection == null) {
            return false;
        }
        
        if (!connection.claimInterface(usbInterface, true)) {
            connection.close();
            return false;
        }

        mConnection = connection;
        
        // 寻找端点
        for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
            UsbEndpoint ep = usbInterface.getEndpoint(i);
            if (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {
                    mEndpointOut = ep;
                } else {
                    mEndpointIn = ep;
                }
            }
        }
        
        // 初始化波特率等 (如果是标准 CDC 可能需要 ControlTransfer 设置 LineCoding)
        // initCdcAcm(connection); 

        return true;
    }

    /**
     * 发送原始字符串指令
     */
    public void sendCmd(String cmd) {
        if (mConnection == null || mEndpointOut == null) return;
        byte[] bytes = cmd.getBytes(StandardCharsets.UTF_8);
        mConnection.bulkTransfer(mEndpointOut, bytes, bytes.length, 100);
    }

    // --- 业务层封装 ---

    public void touchDown(int x, int y) {
        sendCmd("$#md:" + x + "," + y + "|");
    }

    public void touchMove(int x, int y) {
        sendCmd("$#mm:" + x + "," + y + "|");
    }

    public void touchUp(int x, int y) {
        sendCmd("$#mu:" + x + "," + y + "|");
    }
    
    public void close() {
        if (mConnection != null) {
            mConnection.close();
        }
    }
}
```

### 步骤 C: 暴露给 JavaScript 其实 (Global/Module)

在 Auto.js 的初始化逻辑中（例如 `ScriptRuntime` 或 `GlobalObject` 类），将 `FqyUsbDriver` 注入为全局对象或模块。

```java
// 在脚本初始化时
Scriptable scope = ...; // Rhino Scope
FqyUsbDriver driver = new FqyUsbDriver(context);
// 包装成 JS 对象并注入
// ScriptableObject.putProperty(scope, "fqy", Context.javaToJS(driver, scope));
```

这样脚本里就能直接用了：
```javascript
fqy.connect();
fqy.touchDown(500, 500);
```

## 3. 下一步行动

请切换到 **Auto.js 源码项目**，我们将按照上述步骤把代码填进去。
