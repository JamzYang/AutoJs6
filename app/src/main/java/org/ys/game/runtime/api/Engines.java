package org.ys.game.runtime.api;

import org.ys.game.engine.JavaScriptEngine;
import org.ys.game.engine.ScriptEngine;
import org.ys.game.engine.ScriptEngineService;
import org.ys.game.execution.ExecutionConfig;
import org.ys.game.execution.ScriptExecution;
import org.ys.game.runtime.ScriptRuntime;
import org.ys.game.script.AutoFileSource;
import org.ys.game.script.JavaScriptFileSource;
import org.ys.game.script.StringScriptSource;

import java.util.Set;

/**
 * Created by Stardust on Aug 4, 2017.
 */
public class Engines {

    private final ScriptEngineService mEngineService;
    private JavaScriptEngine mScriptEngine;
    private final ScriptRuntime mScriptRuntime;

    public Engines(ScriptEngineService engineService, ScriptRuntime scriptRuntime) {
        mEngineService = engineService;
        mScriptRuntime = scriptRuntime;
    }

    public ScriptExecution execScript(String name, String script, ExecutionConfig config) {
        StringScriptSource scriptSource = new StringScriptSource(name, script);
        scriptSource.setPrefix("$engine/");
        return mEngineService.execute(scriptSource, config);
    }

    public ScriptExecution execScriptFile(String path, ExecutionConfig config) {
        return mEngineService.execute(new JavaScriptFileSource(mScriptRuntime.files.path(path)), config);
    }

    public ScriptExecution execAutoFile(String path, ExecutionConfig config) {
        return mEngineService.execute(new AutoFileSource(mScriptRuntime.files.path(path)), config);
    }

    public Object all() {
        return mScriptRuntime.bridges.toArray(mEngineService.getEngines());
    }

    public int stopAll() {
        return mEngineService.stopAll();
    }

    public void stopAllAndToast() {
        mEngineService.stopAllAndToast();
    }

    public void setCurrentEngine(JavaScriptEngine engine) {
        if (mScriptEngine != null)
            throw new IllegalStateException();
        mScriptEngine = engine;
    }

    public Set<ScriptEngine> getEngines() {
        return mEngineService.getEngines();
    }

    public JavaScriptEngine myEngine() {
        return mScriptEngine;
    }

}
