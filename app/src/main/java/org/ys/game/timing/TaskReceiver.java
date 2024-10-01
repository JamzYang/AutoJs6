package org.ys.game.timing;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.ys.game.external.ScriptIntents;

/**
 * Created by Stardust on Nov 27, 2017.
 */
public class TaskReceiver extends BroadcastReceiver {

    public static final String ACTION_TASK = "org.ys.game.action.task";
    public static final String EXTRA_TASK_ID = "task_id";

    @Override
    public void onReceive(Context context, Intent intent) {
        ScriptIntents.handleIntent(context, intent);
        long id = intent.getLongExtra(EXTRA_TASK_ID, -1);
        if (id >= 0) {
            TimedTaskManager.notifyTaskFinished(id);
        }
    }
}
