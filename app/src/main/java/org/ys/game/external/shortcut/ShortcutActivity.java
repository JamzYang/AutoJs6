package org.ys.game.external.shortcut;

import android.app.Activity;
import android.os.Bundle;

import org.ys.game.external.ScriptIntents;
import org.ys.game.model.script.PathChecker;
import org.ys.game.model.script.ScriptFile;
import org.ys.game.model.script.Scripts;
import org.ys.game.util.ViewUtils;

/**
 * Created by Stardust on Jan 23, 2017.
 */
public class ShortcutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String path = getIntent().getStringExtra(ScriptIntents.EXTRA_KEY_PATH);
        if (new PathChecker(this).checkAndToastError(path)) {
            runScriptFile(path);
        }
        finish();
    }

    private void runScriptFile(String path) {
        try {
            Scripts.run(this, new ScriptFile(path));
        } catch (Exception e) {
            e.printStackTrace();
            ViewUtils.showToast(this, e.getMessage(), true);
        }
    }

}
