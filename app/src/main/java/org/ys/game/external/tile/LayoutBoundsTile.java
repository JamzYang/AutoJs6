package org.ys.game.external.tile;

import android.content.Context;

import org.ys.game.core.accessibility.NodeInfo;
import org.ys.game.ui.floating.FullScreenFloatyWindow;
import org.ys.game.ui.floating.layoutinspector.LayoutBoundsFloatyWindow;

public class LayoutBoundsTile extends LayoutInspectTileService {

    @Override
    protected FullScreenFloatyWindow onCreateWindow(NodeInfo capture, Context context) {
        return new LayoutBoundsFloatyWindow(capture, context, true) {
            @Override
            public void close() {
                super.close();
                updateTile();
            }
        };
    }

}
