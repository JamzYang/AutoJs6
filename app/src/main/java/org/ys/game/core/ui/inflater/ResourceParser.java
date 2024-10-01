package org.ys.game.core.ui.inflater;

import org.ys.game.core.ui.inflater.util.Drawables;

/**
 * Created by Stardust on Jan 24, 2018.
 */
public class ResourceParser {

    private final Drawables mDrawables;

    public ResourceParser(Drawables drawables) {
        mDrawables = drawables;
    }

    public Drawables getDrawables() {
        return mDrawables;
    }

}
