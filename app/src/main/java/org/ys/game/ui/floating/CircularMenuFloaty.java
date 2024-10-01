package org.ys.game.ui.floating;

import org.ys.game.ui.enhancedfloaty.FloatyService;

public interface CircularMenuFloaty {

    CircularActionView inflateActionView(FloatyService service, CircularMenuWindow window);

    CircularActionMenu inflateMenuItems(FloatyService service, CircularMenuWindow window);

}
