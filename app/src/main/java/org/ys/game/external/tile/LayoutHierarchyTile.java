package org.ys.game.external.tile;

import android.content.Context;

import org.ys.game.core.accessibility.NodeInfo;
import org.ys.game.ui.floating.FullScreenFloatyWindow;
import org.ys.game.ui.floating.layoutinspector.LayoutHierarchyFloatyWindow;

public class LayoutHierarchyTile extends LayoutInspectTileService {

    @Override
    protected FullScreenFloatyWindow onCreateWindow(NodeInfo capture, Context context) {
        return new LayoutHierarchyFloatyWindow(capture, context, true) {
            @Override
            public void close() {
                super.close();
                updateTile();
            }
        };
    }
}
