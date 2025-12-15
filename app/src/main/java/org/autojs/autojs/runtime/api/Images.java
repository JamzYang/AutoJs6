package org.autojs.autojs.runtime.api;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.Image;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import org.autojs.autojs.AutoJs;
import org.autojs.autojs.annotation.ScriptInterface;
import org.autojs.autojs.annotation.ScriptVariable;
import org.autojs.autojs.concurrent.VolatileDispose;
import org.autojs.autojs.core.image.CapturedImage;
import org.autojs.autojs.core.image.ImageWrapper;
import org.autojs.autojs.core.image.RhinoColorFinder;
import org.autojs.autojs.core.image.TemplateMatching;
import org.autojs.autojs.core.image.capture.ScreenCaptureRequester;
import org.autojs.autojs.core.image.capture.ScreenCapturer;
import org.autojs.autojs.core.image.capture.ScreenCapturerForegroundService;
import org.autojs.autojs.core.opencv.Mat;
import org.autojs.autojs.core.opencv.OpenCVHelper;
import org.autojs.autojs.core.pref.Language;
import org.autojs.autojs.core.ui.inflater.util.Drawables;
import org.autojs.autojs.pio.UncheckedIOException;
import org.autojs.autojs.runtime.ScriptRuntime;
import org.autojs.autojs.runtime.api.ImageFeatureMatching.FeatureMatchingDescriptor;
import org.autojs.autojs.runtime.exception.WrappedRuntimeException;
import org.autojs.autojs.util.ForegroundServiceUtils;
import org.autojs.autojs.util.BitmapUtils;
import org.autojs.autojs6.R;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.app.Activity.RESULT_OK;
import static org.autojs.autojs.util.RhinoUtils.isMainThread;
import static org.autojs.autojs.util.StringUtils.str;

/**
 * 快速参考 – 图像尺寸 / 质量相关辅助方法
 * <hr>
 * <b>scale</b><br>
 * 在已解码的 Bitmap 上, 按照给定比例同时缩放宽高.<br>
 * <p>
 * <b>resize</b><br>
 * 将已解码的 Bitmap 调整为指定宽高; 可选择保持或忽略纵横比.<br>
 * <p>
 * <b>downsample</b><br>
 * 解码前通过设置 inSampleSize 跳读像素, 显著降低解码分辨率与内存.<br>
 * <p>
 * <b>compress</b><br>
 * 使用指定格式与质量重新编码 Bitmap (或字节流), 以减小磁盘体积.<br>
 * <p>
 * <b>save</b><br>
 * 便捷封装, 内部调用 compress (默认或自定义参数) 后写入存储.<br>
 * <hr>
 * Created by Stardust on May 20, 2017.
 * Modified by SuperMonster003 as of Dec 1, 2021.
 */
public class Images {

    private static final String TAG = Images.class.getSimpleName();
    private static volatile boolean sOpenCvInitialized;

    /**
     * 截屏授权结果的进程级缓存 (MediaProjection 授权返回的 Intent data).
     *
     * 说明:
     * - 该缓存只在当前进程存活期间有效 (App 进程被杀后会丢失).
     * - 缓存的目的是让后续脚本执行可直接复用授权结果创建 ScreenCapturer, 避免重复弹系统授权弹窗.
     */
    @Nullable
    private static volatile Intent sScreenCapturePermissionData;

    @ScriptVariable
    public final RhinoColorFinder colorFinder;

    private final Context mContext;
    private final ScriptRuntime mScriptRuntime;
    private final ScreenMetrics mScreenMetrics;
    private volatile ScreenCapturer.OnScreenCaptureAvailableListener mOnScreenCaptureAvailableListener;
    private Image mPreCapture;
    private CapturedImage mPreCaptureImage;
    private ScreenCapturer mScreenCapturer;
    private ScreenCaptureRequester mScreenCaptureRequester;

    /**
     * 获取进程级缓存的截屏授权 Intent.
     *
     * @return 缓存的授权 Intent 的拷贝, 可能为 null.
     */
    @Nullable
    private static Intent getCachedScreenCapturePermissionData() {
        Intent data = sScreenCapturePermissionData;
        return data == null ? null : new Intent(data);
    }

    /**
     * 写入进程级缓存的截屏授权 Intent.
     *
     * @param data MediaProjection 授权返回的 Intent.
     */
    private static void cacheScreenCapturePermissionData(@NonNull Intent data) {
        sScreenCapturePermissionData = new Intent(data);
    }

    /**
     * 清空进程级缓存的截屏授权 Intent.
     */
    private static void clearCachedScreenCapturePermissionData() {
        sScreenCapturePermissionData = null;
    }

    /**
     * 确保 ScreenCapturerForegroundService 已启动并进入前台.
     *
     * Android 10+ 及 Android 14 对 MediaProjection 的前台服务时序/状态有更严格要求,
     * 这里复用 ScreenCaptureRequester 中的策略:
     * - 先 startService
     * - 如果尚未进入前台, 则 bindService 等待回调后再继续
     */
    private static void ensureScreenCapturerForegroundServiceReady(@NonNull Context applicationContext, @NonNull Runnable onReady) {
        // 超时兜底: 避免 bindService / onServiceConnected 在某些 ROM/时序下不回调, 导致脚本无限 block.
        final long READY_TIMEOUT_MS = 3000L;
        final Handler mainHandler = new Handler(Looper.getMainLooper());
        final AtomicBoolean done = new AtomicBoolean(false);

        applicationContext.startService(new Intent(applicationContext, ScreenCapturerForegroundService.class));
        if (ForegroundServiceUtils.isRunning(applicationContext, ScreenCapturerForegroundService.class)) {
            if (done.compareAndSet(false, true)) {
                mainHandler.post(onReady);
            }
            return;
        }
        final ServiceConnection[] connectionHolder = new ServiceConnection[1];
        final Runnable timeoutRunnable = () -> {
            if (!done.compareAndSet(false, true)) return;
            try {
                if (connectionHolder[0] != null) {
                    applicationContext.unbindService(connectionHolder[0]);
                }
            } catch (Exception ignored) {
                // 忽略解绑异常.
            }
            onReady.run();
        };
        mainHandler.postDelayed(timeoutRunnable, READY_TIMEOUT_MS);

        connectionHolder[0] = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                if (!done.compareAndSet(false, true)) {
                    try {
                        applicationContext.unbindService(connectionHolder[0]);
                    } catch (Exception ignored) {
                        // Ignore.
                    }
                    return;
                }
                mainHandler.removeCallbacks(timeoutRunnable);
                try {
                    applicationContext.unbindService(connectionHolder[0]);
                } catch (Exception ignored) {
                    // 忽略解绑异常 (部分 ROM/时序下可能抛出异常).
                }
                mainHandler.post(onReady);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                /* Empty body. */
            }
        };

        boolean bound = applicationContext.bindService(
                new Intent(applicationContext, ScreenCapturerForegroundService.class),
                connectionHolder[0],
                Context.BIND_AUTO_CREATE
        );

        if (!bound) {
            mainHandler.removeCallbacks(timeoutRunnable);
            if (done.compareAndSet(false, true)) {
                mainHandler.post(onReady);
            }
        }
    }

    public Images(Context context, ScriptRuntime scriptRuntime) {
        mContext = context;
        mScreenMetrics = scriptRuntime.getScreenMetrics();
        mScriptRuntime = scriptRuntime;
        this.colorFinder = new RhinoColorFinder(mScreenMetrics);
    }

    public interface OnScreenCaptureAvailableListener {
        void onCaptureAvailable(@NonNull ImageWrapper imageWrapper);
    }

    @ScriptInterface
    public static void initOpenCvIfNeeded() {
        if (sOpenCvInitialized || OpenCVHelper.isInitialized()) {
            return;
        }
        AutoJs autoJs = AutoJs.getInstance();
        Activity activity = autoJs.getAppUtils().getCurrentActivity();
        Context context = activity == null ? autoJs.getApplication() : activity;
        Log.i(TAG, "OpenCV: initializing");
        if (isMainThread()) {
            OpenCVHelper.initIfNeeded(context, Images::initSuccessfully);
        } else {
            VolatileDispose<Boolean> result = new VolatileDispose<>();
            OpenCVHelper.initIfNeeded(context, () -> {
                initSuccessfully();
                result.setAndNotify(true);
            });
            if (Boolean.FALSE.equals(result.blockedGet(60_000))) {
                throw new RuntimeException("Timed out while initializing OpenCV");
            }
        }
    }

    private static void initSuccessfully() {
        sOpenCvInitialized = true;
        Log.i(TAG, "OpenCV: initialized");
    }

    public static int pixel(ImageWrapper image, int x, int y) {
        if (image == null) {
            throw new NullPointerException(str(R.string.error_method_called_with_null_argument, "Images.pixel", "image"));
        }
        int pixel = image.pixel(x, y);
        image.shoot();
        return pixel;
    }

    public static ImageWrapper concat(ScriptRuntime scriptRuntime, ImageWrapper imgA, ImageWrapper imgB, int direction) {
        if (!Arrays.asList(Gravity.START, Gravity.END, Gravity.TOP, Gravity.BOTTOM).contains(direction)) {
            throw new IllegalArgumentException(str(R.string.error_illegal_argument, "direction", direction));
        }
        int width;
        int height;
        if (direction == Gravity.START || direction == Gravity.TOP) {
            ImageWrapper tmp = imgA;
            imgA = imgB;
            imgB = tmp;
        }
        if (direction == Gravity.START || direction == Gravity.END) {
            width = imgA.getWidth() + imgB.getWidth();
            height = Math.max(imgA.getHeight(), imgB.getHeight());
        } else {
            width = Math.max(imgA.getWidth(), imgB.getHeight());
            height = imgA.getHeight() + imgB.getHeight();
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        if (direction == Gravity.START || direction == Gravity.END) {
            canvas.drawBitmap(imgA.getBitmap(), 0, (float) (height - imgA.getHeight()) / 2, paint);
            canvas.drawBitmap(imgB.getBitmap(), imgA.getWidth(), (float) (height - imgB.getHeight()) / 2, paint);
        } else {
            canvas.drawBitmap(imgA.getBitmap(), (float) (width - imgA.getWidth()) / 2, 0, paint);
            canvas.drawBitmap(imgB.getBitmap(), (float) (width - imgB.getWidth()) / 2, imgA.getHeight(), paint);
        }
        imgA.shoot();
        imgB.shoot();
        return ImageWrapper.ofBitmap(scriptRuntime, bitmap);
    }

    public static void saveBitmap(@NonNull Bitmap bitmap, String path) {
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(path));
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Bitmap scaleBitmap(Bitmap origin, int newWidth, int newHeight) {
        if (origin == null) {
            return null;
        }
        int height = origin.getHeight();
        int width = origin.getWidth();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
    }

    protected static void setImageCaptureCallback(ScriptRuntime scriptRuntime, OnScreenCaptureAvailableListener onScreenCaptureAvailableListener, Image image) {
        ImageWrapper ofImage = ImageWrapper.ofImage(scriptRuntime, image);
        onScreenCaptureAvailableListener.onCaptureAvailable(ofImage);
        // ofImage.recycle();
    }

    public ScriptPromiseAdapter requestScreenCapture(int orientation, int width, int height, boolean isAsync) {
        ScriptPromiseAdapter promiseAdapter = new ScriptPromiseAdapter();
        if (mScreenCapturer != null) {
            promiseAdapter.resolve(true);
            return promiseAdapter;
        }

        Handler handler = isAsync
                ? new Handler(Looper.getMainLooper())
                : new Handler(mScriptRuntime.loopers.getServantLooper());
        ScreenCapturer.Options options = new ScreenCapturer.Options(
                width, height, orientation, ScreenMetrics.getDeviceScreenDensity(), isAsync
        );

        // 优先复用进程级缓存的授权结果, 避免重复弹系统授权.
        Intent cachedPermissionData = getCachedScreenCapturePermissionData();
        if (cachedPermissionData != null) {
            Context applicationContext = AutoJs.getInstance().getApplication().getApplicationContext();
            ensureScreenCapturerForegroundServiceReady(applicationContext, () -> {
                try {
                    mScreenCapturer = new ScreenCapturer(mContext, cachedPermissionData, options, handler);
                    mScreenCapturer.setImageCaptureCallback(mOnScreenCaptureAvailableListener);
                    promiseAdapter.resolve(true);
                } catch (SecurityException ex) {
                    // 缓存可能已失效 (系统回收/用户撤销/ROM 策略等), 清理缓存并回退到正常申请流程.
                    clearCachedScreenCapturePermissionData();
                    requestScreenCaptureByPrompt(handler, options, promiseAdapter);
                }
            });
            return promiseAdapter;
        }

        requestScreenCaptureByPrompt(handler, options, promiseAdapter);
        return promiseAdapter;
    }

    /**
     * 通过系统弹窗申请截屏权限 (MediaProjection).
     */
    private void requestScreenCaptureByPrompt(@NonNull Handler handler, @NonNull ScreenCapturer.Options options, @NonNull ScriptPromiseAdapter promiseAdapter) {
        Context contextForRequest = mScriptRuntime.app.getCurrentActivity();
        if (contextForRequest == null) contextForRequest = mContext;
        mScreenCaptureRequester = new ScreenCaptureRequester();
        mScreenCaptureRequester.request(contextForRequest, new ScreenCaptureRequester.Callback() {
            @Override
            public void onRequestResult(int resultCode, @Nullable Intent intent) {
                try {
                    if (resultCode == RESULT_OK && intent != null) {
                        cacheScreenCapturePermissionData(intent);
                        mScreenCapturer = new ScreenCapturer(mContext, intent, options, handler);
                        mScreenCapturer.setImageCaptureCallback(mOnScreenCaptureAvailableListener);
                        promiseAdapter.resolve(true);
                    } else {
                        promiseAdapter.resolve(false);
                    }
                } catch (SecurityException ex) {
                    promiseAdapter.reject(ex);
                } finally {
                    // 申请流程结束后释放 requester, 避免 bindService 未解绑导致泄漏.
                    releaseScreenCaptureRequester();
                }
            }

            @Override
            public void onRequestError(@NonNull Throwable t) {
                try {
                    promiseAdapter.reject(t);
                } finally {
                    // 发生错误同样需要释放 requester.
                    releaseScreenCaptureRequester();
                }
            }
        });
    }

    /**
     * 执行一次截屏并返回截图图像.
     *
     * 说明:
     * - 若当前脚本未创建 ScreenCapturer, 将优先尝试使用进程级缓存的授权结果进行懒加载.
     * - 若没有缓存或缓存失效, 将抛出无权限异常, 脚本需先调用 requestScreenCapture().
     */
    @Nullable
    public ImageWrapper captureScreen() {
        synchronized (this) {
            if (mScreenCapturer == null) {
                Intent cachedPermissionData = getCachedScreenCapturePermissionData();
                if (cachedPermissionData != null) {
                    Handler handler = new Handler(mScriptRuntime.loopers.getServantLooper());
                    ScreenCapturer.Options options = new ScreenCapturer.Options(
                            -1, -1, ScreenCapturer.ORIENTATION_AUTO, ScreenMetrics.getDeviceScreenDensity(), false
                    );
                    Context applicationContext = AutoJs.getInstance().getApplication().getApplicationContext();
                    ensureScreenCapturerForegroundServiceReady(applicationContext, () -> {
                        try {
                            mScreenCapturer = new ScreenCapturer(mContext, cachedPermissionData, options, handler);
                            mScreenCapturer.setImageCaptureCallback(mOnScreenCaptureAvailableListener);
                        } catch (SecurityException ex) {
                            clearCachedScreenCapturePermissionData();
                        }
                    });
                }
            }

            if (mScreenCapturer == null) {
                throw new SecurityException(mContext.getString(R.string.error_no_screen_capture_permission));
            }
            Image capture = mScreenCapturer.capture();
            if (capture != mPreCapture || mPreCaptureImage == null) {
                mPreCapture = capture;
                if (mPreCaptureImage != null) {
                    mPreCaptureImage.recycleInternal();
                }
                if (capture != null) {
                    mPreCaptureImage = new CapturedImage(mScriptRuntime, capture);
                }
            }
            return mPreCaptureImage;
        }
    }

    public boolean captureScreen(String path) {
        ImageWrapper image = captureScreen();
        return image != null && image.saveTo(path);
    }

    public ImageWrapper copy(@NonNull ImageWrapper image) {
        ImageWrapper imageWrapper = image.clone();
        image.shoot();
        return imageWrapper;
    }

    public boolean save(@NonNull ImageWrapper image, @NonNull String path, @NonNull String format, int quality) throws IOException {
        Bitmap bitmap = image.getBitmap();
        Bitmap.CompressFormat compressFormat = parseImageFormat(format);

        if (compressFormat == Bitmap.CompressFormat.PNG && quality != 100) {
            byte[] compressed = PngQuantBridge.quantize(bitmap, quality);
            if (compressed != null) {
                try (FileOutputStream fos = new FileOutputStream(path)) {
                    fos.write(compressed);
                    return true;
                }
            }
            throw new WrappedRuntimeException("PNG quantization failed");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (compressFormat == Bitmap.CompressFormat.WEBP_LOSSLESS && quality != 100) {
                throw new IllegalArgumentException(mContext.getString(R.string.error_webp_lossless_quality_not_supported));
            }
        }

        try (FileOutputStream fos = new FileOutputStream(path)) {
            boolean b = bitmap.compress(compressFormat, quality, fos);
            image.shoot();
            return b;
        }
    }

    public byte[] compressToBytes(@NotNull ImageWrapper image, @NotNull String format, int quality) {
        Bitmap bitmap = image.getBitmap();
        Bitmap.CompressFormat compressFormat = parseImageFormat(format);

        if (compressFormat == Bitmap.CompressFormat.PNG && quality != 100) {
            byte[] compressed = PngQuantBridge.quantize(bitmap, quality);
            image.shoot();
            if (compressed != null) {
                return compressed;
            }
            throw new WrappedRuntimeException("PNG quantization failed");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (compressFormat == Bitmap.CompressFormat.WEBP_LOSSLESS && quality != 100) {
                image.shoot();
                throw new IllegalArgumentException(mContext.getString(R.string.error_webp_lossless_quality_not_supported));
            }
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(compressFormat, quality, outputStream);
        image.shoot();
        return outputStream.toByteArray();
    }

    public ImageWrapper compress(@NotNull ImageWrapper image, @NotNull String format, int quality) throws BitmapUtils.DecodeException {
        return fromBytes(compressToBytes(image, format, quality));
    }

    // public EventEmitter select() {
    //     EventEmitter eventEmitter = new EventEmitter(scriptRuntime.bridges, scriptRuntime.timers.getTimerForCurrentThread());
    //     StartForResultActivity.start(mContext, new SelectImageCallback(this, eventEmitter, mContext, mContext.getString(R.string.text_select_image)));
    //     return eventEmitter;
    // }

    public void stopScreenCapture() {
        releaseScreenCapturer();
    }

    public ImageWrapper rotate(@NonNull ImageWrapper img, float x, float y, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree, x, y);
        ImageWrapper imageWrapper = ImageWrapper.ofBitmap(mScriptRuntime, Bitmap.createBitmap(img.getBitmap(), 0, 0, img.getWidth(), img.getHeight(), matrix, true));
        img.shoot();
        return imageWrapper;
    }

    @ScriptInterface
    public ImageWrapper flip(@NonNull ImageWrapper img, boolean horizontal, boolean vertical) {
        Bitmap original = img.getBitmap();
        Matrix matrix = new Matrix();

        // Set scaling ratio according to input parameters.
        // zh-CN: 根据传入参数设置缩放比例.
        float sx = horizontal ? -1.0f : 1.0f;
        float sy = vertical ? -1.0f : 1.0f;
        matrix.preScale(sx, sy);

        // After horizontal flip, translate to image width position.
        // zh-CN: 水平方向翻转后, 平移到图像宽度的位置.
        if (horizontal) matrix.postTranslate(original.getWidth(), 0);

        // After vertical flip, translate to image height position.
        // zh-CN: 垂直方向翻转后, 平移到图像高度的位置.
        if (vertical) matrix.postTranslate(0, original.getHeight());

        Bitmap flipped = Bitmap.createBitmap(original, 0, 0, original.getWidth(), original.getHeight(), matrix, true);
        img.shoot();
        return ImageWrapper.ofBitmap(mScriptRuntime, flipped);
    }

    public ImageWrapper clip(@NonNull ImageWrapper img, int x, int y, int w, int h) {
        ImageWrapper imageWrapper = ImageWrapper.ofBitmap(mScriptRuntime, Bitmap.createBitmap(img.getBitmap(), x, y, w, h));
        img.shoot();
        return imageWrapper;
    }

    public ImageWrapper read(String path) {
        return read(path, false);
    }

    @Nullable
    public ImageWrapper read(String path, boolean isStrict) {
        var fullPath = mScriptRuntime.files.path(path);
        Bitmap bitmap = BitmapFactory.decodeFile(fullPath);
        if (bitmap == null) {
            if (!isStrict) return null;
            throw new RuntimeException(mContext.getString(R.string.error_file_in_path_does_not_exist, fullPath));
        }
        return ImageWrapper.ofBitmap(mScriptRuntime, bitmap);
    }

    @NotNull
    @Contract("_ -> new")
    public static Mat imread(String path) {
        initOpenCvIfNeeded();
        return new Mat(Imgcodecs.imread(path).nativeObj);
    }

    public ImageWrapper fromBase64(String data) {
        return ImageWrapper.ofBitmap(mScriptRuntime, Drawables.loadBase64Data(data));
    }

    public String toBase64(ImageWrapper img, String format, int quality) {
        byte[] input = toBytes(img, format, quality);
        img.shoot();
        return Base64.encodeToString(input, Base64.NO_WRAP);
    }

    public byte[] toBytes(@NonNull ImageWrapper img, String format, int quality) {
        Bitmap.CompressFormat compressFormat = parseImageFormat(format);
        Bitmap bitmap = img.getBitmap();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(compressFormat, quality, outputStream);
        img.shoot();
        return outputStream.toByteArray();
    }

    public ImageWrapper fromBytes(byte[] bytes) throws BitmapUtils.DecodeException {
        return ImageWrapper.ofBitmap(mScriptRuntime, BitmapUtils.bitmapFromByteArrayOrThrow(bytes));
    }

    private Bitmap.CompressFormat parseImageFormat(String format) {
        return switch (format.toLowerCase(Language.getPrefLanguage().getLocale())) {
            case "png" -> Bitmap.CompressFormat.PNG;
            case "jpeg", "jpg" -> Bitmap.CompressFormat.JPEG;
            case "webp" -> Bitmap.CompressFormat.WEBP;
            case "webp_lossy", "webp-lossy" -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    yield Bitmap.CompressFormat.WEBP_LOSSY;
                }
                throw new IllegalArgumentException("Image format \"WEBP_LOSSY\" only supports on Android API Level 30 (11) [R] and above");
            }
            case "webp_lossless", "webp-lossless" -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    yield Bitmap.CompressFormat.WEBP_LOSSLESS;
                }
                throw new IllegalArgumentException("Image format \"WEBP_LOSSLESS\" only supports on Android API Level 30 (11) [R] and above");
            }
            default -> throw new IllegalArgumentException(str(R.string.error_illegal_argument, "format", format));
        };
    }

    public ImageWrapper load(String src) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(src).openConnection();
            connection.setDoInput(true);
            connection.connect();
            return ImageWrapper.ofBitmap(mScriptRuntime, BitmapFactory.decodeStream(connection.getInputStream()));
        } catch (IOException e) {
            return null;
        }
    }

    public ImageWrapper invert(@NonNull ImageWrapper image) {
        initOpenCvIfNeeded();

        Bitmap originalBitmap = image.getBitmap();
        int originalWidth = originalBitmap.getWidth();
        int originalHeight = originalBitmap.getHeight();

        org.opencv.core.Mat srcMat = new org.opencv.core.Mat();
        Utils.bitmapToMat(originalBitmap, srcMat);

        // 分离原始 Mat 的通道
        List<org.opencv.core.Mat> channels = new ArrayList<>(4);
        Core.split(srcMat, channels);
        org.opencv.core.Mat alphaChannel = channels.get(3); // 提取 alpha 通道

        // 对 BGR 通道进行反色操作
        org.opencv.core.Mat bgrMat = new org.opencv.core.Mat();
        Imgproc.cvtColor(srcMat, bgrMat, Imgproc.COLOR_BGRA2BGR);
        org.opencv.core.Mat invertedBGRMat = new org.opencv.core.Mat();
        Core.bitwise_not(bgrMat, invertedBGRMat);

        // 合并反色后的 BGR 通道与原始的 alpha 通道
        channels.set(0, new org.opencv.core.Mat());
        channels.set(1, new org.opencv.core.Mat());
        channels.set(2, new org.opencv.core.Mat());
        channels.subList(0, 3).clear(); // 清除前 3 个通道并加入反色后的 BGR 通道
        Core.split(invertedBGRMat, channels); // 拆分并自动添加到 channels 的前 3 个位置
        channels.add(3, alphaChannel); // 将原来的 alpha 通道放回最后一个位置

        org.opencv.core.Mat destMat = new org.opencv.core.Mat();
        Core.merge(channels, destMat); // 合并 4 个通道 (BGR + 原始 alpha)

        // 转换反色后的 Mat 回 Bitmap
        Bitmap invertedBitmap = Bitmap.createBitmap(originalWidth, originalHeight, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(destMat, invertedBitmap);

        image.shoot();

        return ImageWrapper.ofBitmap(mScriptRuntime, invertedBitmap);
    }

    public void releaseScreenCapturer() {
        synchronized (this) {
            if (mScreenCapturer != null) {
                mScreenCapturer.release();
                mScreenCapturer = null;
            }
            if (mPreCapture != null) {
                mPreCapture.close();
                mPreCapture = null;
            }
            if (mPreCaptureImage != null) {
                mPreCaptureImage.recycleInternal();
                mPreCaptureImage = null;
            }
            releaseScreenCaptureRequester();
        }
    }

    public void stopScreenCapturerForegroundService() {
        var applicationContext = AutoJs.getInstance().getApplication().getApplicationContext();
        applicationContext.stopService(new Intent(applicationContext, ScreenCapturerForegroundService.class));
        releaseScreenCaptureRequester();
    }

    private void releaseScreenCaptureRequester() {
        if (mScreenCaptureRequester != null) {
            mScreenCaptureRequester.unbindService();
            mScreenCaptureRequester = null;
        }
    }

    @ScriptInterface
    public Point findImage(ImageWrapper image, ImageWrapper template) throws Exception {
        return findImage(image, template, 0.9f, null);
    }

    @ScriptInterface
    public Point findImage(ImageWrapper image, ImageWrapper template, float threshold) throws Exception {
        return findImage(image, template, threshold, null);
    }

    @ScriptInterface
    public Point findImage(ImageWrapper image, ImageWrapper template, float threshold, Rect rect) throws Exception {
        return findImage(image, template, 0.7f, threshold, rect, TemplateMatching.MAX_LEVEL_AUTO);
    }

    @Nullable
    @ScriptInterface
    public Point findImage(ImageWrapper image, ImageWrapper template, float weakThreshold, float strictThreshold, Rect rect, int maxLevel) throws Exception {
        initOpenCvIfNeeded();
        if (image == null) {
            throw new NullPointerException(mContext.getString(R.string.error_method_called_with_null_argument, "Images.findImage", "image"));
        }
        if (template == null) {
            throw new NullPointerException(mContext.getString(R.string.error_method_called_with_null_argument, "Images.findImage", "template"));
        }
        Mat src = image.getMat();
        boolean shouldReleaseMat = false;
        if (rect != null) {
            if (template.getWidth() > rect.width) {
                throw new Exception(mContext.getString(R.string.error_excessive_width_for_template_n_region, template.getWidth(), rect.width));
            }
            if (template.getHeight() > rect.height) {
                throw new Exception(mContext.getString(R.string.error_excessive_height_for_template_n_region, template.getHeight(), rect.height));
            }
            src = new Mat(src, rect);
            shouldReleaseMat = true;
        }
        @Nullable
        org.opencv.core.Point point;
        try {
            point = TemplateMatching.singleTemplateMatching(
                    src,
                    template.getMat(),
                    new TemplateMatching.Options(-1, weakThreshold, strictThreshold, maxLevel)
            );
        } finally {
            if (shouldReleaseMat) {
                OpenCVHelper.release(src);
            }
            image.shoot();
            template.shoot();
        }
        if (point != null) {
            if (rect != null) {
                point.x += rect.x;
                point.y += rect.y;
            }
            point.x = mScreenMetrics.scaleX((int) point.x);
            point.y = mScreenMetrics.scaleX((int) point.y);
        }
        return point;
    }

    public List<TemplateMatching.Match> matchTemplate(ImageWrapper image, ImageWrapper template, float weakThreshold, float strictThreshold, Rect rect, int maxLevel, int limit, boolean useTransparentMask) {
        initOpenCvIfNeeded();
        if (image == null) {
            throw new NullPointerException(mContext.getString(R.string.error_method_called_with_null_argument, "Images.matchTemplate", "image"));
        }
        if (template == null) {
            throw new NullPointerException(mContext.getString(R.string.error_method_called_with_null_argument, "Images.matchTemplate", "template"));
        }
        Mat src = image.getMat();
        if (rect != null) {
            src = new Mat(src, rect);
        }
        List<TemplateMatching.Match> result = TemplateMatching.fastTemplateMatching(
                src,
                template.getMat(),
                new TemplateMatching.Options(-1, weakThreshold, strictThreshold, maxLevel, useTransparentMask, limit)
        );

        if (src != image.getMat()) {
            OpenCVHelper.release(src);
        }
        image.shoot();
        template.shoot();

        for (TemplateMatching.Match match : result) {
            Point point = match.point;
            if (rect != null) {
                point.x += rect.x;
                point.y += rect.y;
            }
            point.x = mScreenMetrics.scaleX((int) point.x);
            point.y = mScreenMetrics.scaleX((int) point.y);
        }
        return result;
    }

    public Mat newMat() {
        return new Mat();
    }

    public Mat newMat(Mat mat, Rect rect) {
        return new Mat(mat, rect);
    }

    @ScriptInterface
    public FeatureMatchingDescriptor detectAndComputeFeatures(Mat mat, float scale, int cvtColor, int method) {
        return ImageFeatureMatching.createFeatureMatchingDescriptor(mat, cvtColor, scale, method);
    }

    public ScreenCapturer getScreenCapturer() {
        return mScreenCapturer;
    }

    @Nullable
    public ScreenCapturer.Options getScreenCaptureOptions() {
        synchronized (this) {
            return mScreenCapturer != null ? mScreenCapturer.getOptions() : null;
        }
    }

    public void setImageCaptureCallback(OnScreenCaptureAvailableListener onScreenCaptureAvailableListener) {
        mOnScreenCaptureAvailableListener = new ScreenCaptureAvailableHandler(mScriptRuntime, onScreenCaptureAvailableListener);
        if (mScreenCapturer != null) {
            mScreenCapturer.setImageCaptureCallback(mOnScreenCaptureAvailableListener);
        }
    }

}