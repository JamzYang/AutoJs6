package org.ys.game.execution;

import org.ys.game.core.looper.Loopers;
import org.ys.game.engine.LoopBasedJavaScriptEngine;
import org.ys.game.engine.ScriptEngine;
import org.ys.game.engine.ScriptEngineManager;
import org.ys.game.inrt.autojs.LoopBasedJavaScriptEngineWithDecryption;
import org.ys.game.script.JavaScriptSource;
import org.ys.gamecat.BuildConfig;

/**
 * Created by Stardust on Oct 27, 2017.
 */
public class LoopedBasedJavaScriptExecution extends RunnableScriptExecution {

    public LoopedBasedJavaScriptExecution(ScriptEngineManager manager, ScriptExecutionTask task) {
        super(manager, task);
    }

    protected Object doExecution(final ScriptEngine engine) {
        engine.setTag(ScriptEngine.TAG_SOURCE, getSource());
        getListener().onStart(this);
        long delay = getConfig().getDelay();
        sleep(delay);
        final long interval = getConfig().getInterval();

        var javaScriptEngine = BuildConfig.isInrt
                ? (LoopBasedJavaScriptEngineWithDecryption) engine
                : (LoopBasedJavaScriptEngine) engine;
        javaScriptEngine.getRuntime().loopers.setMainLooperQuitHandler(new Loopers.LooperQuitHandler() {
            long times = getConfig().getLoopTimes() == 0 ? Integer.MAX_VALUE : getConfig().getLoopTimes();

            @Override
            public boolean shouldQuit() {
                times--;
                if (times > 0) {
                    sleep(interval);
                    javaScriptEngine.execute(getSource());
                    return false;
                }
                javaScriptEngine.getRuntime().loopers.setMainLooperQuitHandler(null);
                return true;
            }
        });
        javaScriptEngine.execute(getSource());

        return null;
    }

    @Override
    public JavaScriptSource getSource() {
        return (JavaScriptSource) super.getSource();
    }

}
