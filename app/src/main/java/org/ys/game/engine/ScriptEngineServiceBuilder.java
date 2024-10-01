package org.ys.game.engine;

import android.content.Context;

import org.ys.game.runtime.api.Console;
import org.ys.game.tool.UiHandler;

/**
 * Created by Stardust on Apr 2, 2017.
 */
public class ScriptEngineServiceBuilder {

    ScriptEngineManager mScriptEngineManager;
    Console mGlobalConsole;
    UiHandler mUiHandler;

    public ScriptEngineServiceBuilder() {

    }

    public ScriptEngineServiceBuilder uiHandler(UiHandler uiHandler) {
        mUiHandler = uiHandler;
        return this;
    }

    public ScriptEngineServiceBuilder engineManger(ScriptEngineManager manager) {
        mScriptEngineManager = manager;
        return this;
    }

    public ScriptEngineServiceBuilder globalConsole(Console console) {
        mGlobalConsole = console;
        return this;
    }

    public ScriptEngineService build() {
        return new ScriptEngineService(this);
    }


}
