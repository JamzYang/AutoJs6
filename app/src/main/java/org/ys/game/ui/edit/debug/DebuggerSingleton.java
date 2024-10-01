package org.ys.game.ui.edit.debug;

import org.ys.game.AutoJs;
import org.ys.game.rhino.debug.Debugger;
import org.mozilla.javascript.ContextFactory;

public class DebuggerSingleton {

    private static final Debugger sDebugger = new Debugger(AutoJs.getInstance().getScriptEngineService(), ContextFactory.getGlobal());

    public static Debugger get(){
        return sDebugger;
    }

}
