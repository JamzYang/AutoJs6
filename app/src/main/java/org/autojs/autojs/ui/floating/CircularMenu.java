package org.autojs.autojs.ui.floating;

import static org.autojs.autojs.ui.Constants.SHARED_PREF_SCRIPT_VERSION;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.afollestad.materialdialogs.MaterialDialog;
import com.makeramen.roundedimageview.RoundedImageView;
import java.io.*;
import java.text.MessageFormat;
import java.util.Objects;
import okhttp3.ResponseBody;
import org.autojs.autojs.AutoJs;
import org.autojs.autojs.app.AppLevelThemeDialogBuilder;
import org.autojs.autojs.app.DialogUtils;
import org.autojs.autojs.core.accessibility.AccessibilityTool;
import org.autojs.autojs.core.accessibility.LayoutInspector;
import org.autojs.autojs.core.accessibility.NodeInfo;
import org.autojs.autojs.core.record.GlobalActionRecorder;
import org.autojs.autojs.core.record.Recorder;
import org.autojs.autojs.model.explorer.ExplorerDirPage;
import org.autojs.autojs.model.explorer.Explorers;
import org.autojs.autojs.model.script.ScriptFile;
import org.autojs.autojs.model.script.Scripts;
import org.autojs.autojs.network.api.ScriptApi;
import org.autojs.autojs.pref.Language;
import org.autojs.autojs.pref.Pref;
import org.autojs.autojs.tool.Func1;
import org.autojs.autojs.ui.Constants;
import org.autojs.autojs.ui.enhancedfloaty.FloatyService;
import org.autojs.autojs.ui.enhancedfloaty.FloatyWindow;
import org.autojs.autojs.ui.explorer.ExplorerView;
import org.autojs.autojs.util.ViewUtils;
import org.autojs.autojs.util.WorkingDirectoryUtils;
import org.autojs.autojs6.R;
import org.autojs.autojs6.databinding.CircularActionMenuBinding;
import org.greenrobot.eventbus.EventBus;
import org.jdeferred.Deferred;
import org.jdeferred.impl.DeferredObject;
import org.jetbrains.annotations.NotNull;
import org.autojs.autojs.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Stardust on Oct 18, 2017.
 */
public class CircularMenu implements Recorder.OnStateChangedListener, LayoutInspector.CaptureAvailableListener {

    public record StateChangeEvent(int currentState, int previousState) {
        /* Empty record body. */
    }

    public static final int STATE_CLOSED = -1;
    public static final int STATE_NORMAL = 0;
    public static final int STATE_RECORDING = 1;

    CircularMenuWindow mWindow;

    private int mState;
    private RoundedImageView mActionViewIcon;
    private final Context mContext;
    private final GlobalActionRecorder mRecorder;
    private CircularActionMenuBinding binding;
    private MaterialDialog mSettingsDialog;
    private MaterialDialog mLayoutInspectDialog;
    private String mRunningPackage, mRunningActivity;
    private Deferred<NodeInfo, Void, Void> mCaptureDeferred;
    private final AccessibilityTool.Service mA11yToolService;

    public CircularMenu(Context context) {
        // mContext = new ContextThemeWrapper(context, R.style.AppTheme);
        mContext = context;
        initFloaty();
        setupWindowListeners();
        mRecorder = GlobalActionRecorder.getSingleton(context);
        mRecorder.addOnStateChangedListener(this);
        AutoJs.getInstance().getLayoutInspector().addCaptureAvailableListener(this);
        mA11yToolService = new AccessibilityTool(mContext).getService();
    }

    private void setupWindowListeners() {
        mWindow.setOnActionViewClickListener(v -> {
            if (isRecording()) {
                stopRecord();
            } else if (mWindow.isExpanded()) {
                mWindow.collapse();
            } else {
                mCaptureDeferred = new DeferredObject<>();
                AutoJs.getInstance().getLayoutInspector().captureCurrentWindow();
                mWindow.expand();
            }
        });
    }

    private void setupBindingListeners() {
        binding.scriptList.setOnClickListener(v -> {
            mWindow.collapse();
            ExplorerView explorerView = new ExplorerView(mContext);
            explorerView.setExplorer(Explorers.workspace(), ExplorerDirPage.createRoot(WorkingDirectoryUtils.getPath()));
            explorerView.setDirectorySpanSize(2);
            final MaterialDialog dialog = new AppLevelThemeDialogBuilder(mContext)
                    .title(mContext.getString(R.string.text_run_script))
                    .customView(explorerView, false)
                    .positiveText(mContext.getString(R.string.text_cancel))
                    .cancelable(false)
                    .build();
            explorerView.setOnItemOperateListener(item -> dialog.dismiss());
            explorerView.setOnItemClickListener((view, item) -> Scripts.run(mContext, item.toScriptFile()));
            explorerView.setOnProjectToolbarOperateListener(toolbar -> dialog.dismiss());
            explorerView.setOnProjectToolbarClickListener(toolbar -> toolbar.findViewById(R.id.project_run).performClick());
            explorerView.setProjectToolbarRunnableOnly(true);
            DialogUtils.adaptToExplorer(dialog, explorerView);
            DialogUtils.showDialog(dialog);
        });

        binding.stopAllScripts.setOnClickListener(v -> {
            mWindow.collapse();
            if (AutoJs.getInstance().getScriptEngineService().stopAllAndToast() <= 0) {
                ViewUtils.showToast(mContext, mContext.getString(R.string.text_no_scripts_to_stop_running));
            }
        });

        binding.runScript.setOnClickListener(v -> {
            mWindow.collapse();
            runSpecificScript();
        });
    }

    private void runSpecificScript() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("autojs.localstorage.script_config", Context.MODE_PRIVATE);
        String currentVersion = sharedPreferences.getString(SHARED_PREF_SCRIPT_VERSION, "");

        ScriptApi scriptApi = RetrofitClient.getClient().create(ScriptApi.class);
        
        scriptApi.getScriptVersion().enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                if (response.isSuccessful()) {
                    String serverVersion = response.body();
                    if (!Objects.equals(currentVersion, serverVersion)) {
                        downloadAndRunScript(scriptApi, serverVersion, sharedPreferences);
                    } else {
                        runExistingScript();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(CircularMenu.class.getSimpleName(), "检查脚本版本失败", t);
                runExistingScript();
            }
        });
    }

    private void downloadAndRunScript(ScriptApi scriptApi, String serverVersion, SharedPreferences sharedPreferences) {
        scriptApi.downloadScript().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    ResponseBody body = response.body();
                    try {
                      assert body != null;
                      writeResponseBodyToDisk(body);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(SHARED_PREF_SCRIPT_VERSION, serverVersion);
                        editor.apply();

                        runExistingScript();
                    } catch (IOException e) {
                        Log.e(CircularMenu.class.getSimpleName(), "保存脚本失败", e);
                        runExistingScript();
                    }
                } else {
                    runExistingScript();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(CircularMenu.class.getSimpleName(), "下载脚本失败", t);
                runExistingScript();
            }
        });
    }

    private boolean writeResponseBodyToDisk(ResponseBody body) throws IOException {
            // 创建一个文件对象，指定下载保存的位置
            File file = new File(mContext.getFilesDir().getPath() + "/main.js");
            InputStream inputStream = null;
            FileOutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                }
                outputStream.flush();
                return true;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
    }

    private void runExistingScript() {
        try {
            Scripts.run(mContext, new ScriptFile(mContext.getFilesDir().getPath()+"/main.js"));
        } catch (Exception e) {
            Log.e(CircularMenu.class.getSimpleName(),"运行脚本错误",e);
            ViewUtils.showToast(mContext, e.getMessage(), true);
        }
    }

    public boolean isRecording() {
        return mState == STATE_RECORDING;
    }

    private void initFloaty() {
        mWindow = new CircularMenuWindow(new CircularMenuFloaty() {
            @Override
            public CircularActionView inflateActionView(FloatyService service, CircularMenuWindow window) {
                CircularActionView actionView = (CircularActionView) View.inflate(service, R.layout.circular_action_view, null);
                mActionViewIcon = actionView.findViewById(R.id.icon);
                return actionView;
            }

            @Override
            public CircularActionMenu inflateMenuItems(FloatyService service, CircularMenuWindow window) {
                CircularActionMenu menu = (CircularActionMenu) View.inflate(new ContextThemeWrapper(service, R.style.AppTheme), R.layout.circular_action_menu, null);
                binding = CircularActionMenuBinding.bind(menu);
                setupBindingListeners();
                return menu;
            }
        });
        mWindow.setKeepToSideHiddenWidthRadio(0.25f);
        FloatyService.addWindow(mWindow);
    }

    private void setState(int state) {
        int previousState = mState;

        mState = state;
        mActionViewIcon.setImageResource(isRecording()
                ? R.drawable.ic_ali_record
                : R.drawable.autojs6_material);
        mActionViewIcon.setBackgroundTintList(ColorStateList.valueOf(mContext.getColor(isRecording()
                ? R.color.circular_menu_icon_red
                : R.color.circular_menu_icon_white)));
        int padding = (int) mContext.getResources().getDimension(isRecording()
                ? R.dimen.padding_circular_menu_recording
                : R.dimen.padding_circular_menu_normal);
        mActionViewIcon.setPadding(padding, padding, padding, padding);

        EventBus.getDefault().post(new StateChangeEvent(mState, previousState));
    }

    public void stopRecord() {
        mRecorder.stop();
    }

    private void inspectLayout(Func1<NodeInfo, FloatyWindow> windowCreator) {
        if (mLayoutInspectDialog != null) {
            mLayoutInspectDialog.dismiss();
            mLayoutInspectDialog = null;
        }
        if (!mA11yToolService.isRunning()) {
            if (!mA11yToolService.start(false)) {
                ViewUtils.showToast(mContext, mContext.getString(R.string.error_no_accessibility_permission_to_capture));
                getAccessibilityTool().launchSettings();
            }
        } else {
            mCaptureDeferred.promise().then(capture -> {
                mActionViewIcon.post(() -> FloatyService.addWindow(windowCreator.call(capture)));
            });
        }
    }

    public void closeAndSaveState(boolean state) {
        Pref.putBooleanSync(R.string.key_floating_menu_shown, state);
        savePosition();
        close();
    }

    public void savePosition() {
        mWindow.savePosition();
    }

    public void savePosition(@NotNull Configuration newConfig) {
        mWindow.savePosition(newConfig);
    }

    private AccessibilityTool getAccessibilityTool() {
        return new AccessibilityTool(mContext);
    }

    private String getRunningPackage() {
        if (!TextUtils.isEmpty(mRunningPackage)) {
            return mRunningPackage;
        }
        return getEmptyInfoHint();
    }

    private String getRunningActivity() {
        if (!TextUtils.isEmpty(mRunningActivity)) {
            return mRunningActivity;
        }
        return getEmptyInfoHint();
    }

    private String getEmptyInfoHint() {
        return MessageFormat.format("{0} ({1})",
                mContext.getString(R.string.text_null).toLowerCase(Language.getPrefLanguage().getLocale()),
                mContext.getString(R.string.text_a11y_service_may_be_needed).toLowerCase(Language.getPrefLanguage().getLocale()));
    }

    @Override
    public void onCaptureAvailable(NodeInfo capture, @NonNull Context context) {
        if (mCaptureDeferred != null && mCaptureDeferred.isPending())
            mCaptureDeferred.resolve(capture);
    }

    private void dismissSettingsDialog() {
        if (mSettingsDialog != null) {
            mSettingsDialog.dismiss();
            mSettingsDialog = null;
        }
    }

    @NonNull
    private String getTextAlreadyCopied(int actionResId) {
        return MessageFormat.format("{0} ({1})",
                mContext.getString(R.string.text_already_copied_to_clip),
                mContext.getString(actionResId).toLowerCase(Language.getPrefLanguage().getLocale()));
    }

    public void close() {
        dismissSettingsDialog();
        try {
            mWindow.close();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            EventBus.getDefault().post(new StateChangeEvent(STATE_CLOSED, mState));
            mState = STATE_CLOSED;
        }
        mRecorder.removeOnStateChangedListener(this);
        AutoJs.getInstance().getLayoutInspector().removeCaptureAvailableListener(this);
    }

    @Override
    public void onStart() {
        setState(STATE_RECORDING);
    }

    @Override
    public void onStop() {
        setState(STATE_NORMAL);
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onResume() {

    }

}
