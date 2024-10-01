package org.ys.game.core.broadcast;

import org.ys.game.core.eventloop.EventEmitter;
import org.ys.game.core.looper.Timer;
import org.ys.game.runtime.ScriptBridges;

/**
 * Created by Stardust on Apr 1, 2018.
 */
public class BroadcastEmitter extends EventEmitter {

    public BroadcastEmitter(ScriptBridges bridges, Timer timer) {
        super(bridges, timer);
        Broadcast.registerListener(this);
    }

    public boolean onBroadcast(String eventName, Object... args) {
        return super.emit(eventName, args);
    }

    public void unregister() {
        Broadcast.unregisterListener(this);
    }

    @Override
    public boolean emit(String eventName, Object... args) {
        Broadcast.send(eventName, args);
        return true;
    }
}
