package org.ys.game.external.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.ys.game.AutoJs;
import org.ys.game.execution.ExecutionConfig;
import org.ys.game.model.script.ScriptFile;
import org.ys.game.timing.IntentTask;
import org.ys.game.timing.TimedTaskManager;
import org.ys.game.util.ViewUtils;

import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BaseBroadcastReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = "BaseBroadcastReceiver";

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SuppressLint("CheckResult")
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "onReceive: intent = " + intent + ", this = " + this);
        try {
            TimedTaskManager.getIntentTaskOfAction(intent.getAction())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(intentTask -> runTask(context, intent, intentTask), Throwable::printStackTrace);
        } catch (Exception e) {
            ViewUtils.showToast(context, e.getMessage(), true);
        }
    }

    static void runTask(Context context, Intent intent, IntentTask task) {
        Log.d(LOG_TAG, "runTask: action = " + intent.getAction() + ", script = " + task.getScriptPath());
        ScriptFile file = new ScriptFile(task.getScriptPath());
        ExecutionConfig config = new ExecutionConfig();
        config.setArgument("intent", intent.clone());
        config.setWorkingDirectory(Objects.requireNonNull(file.getParent()));
        try {
            AutoJs.getInstance().getScriptEngineService().execute(file.toSource(), config);
        } catch (Exception e) {
            e.printStackTrace();
            ViewUtils.showToast(context, e.getMessage(), true);
        }
    }

}
