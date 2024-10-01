package org.ys.game.core.ui.attribute

import android.view.View
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.widget.JsDrawerLayout

class JsDrawerLayoutAttributes(resourceParser: ResourceParser, view: View) : DrawerLayoutAttributes(resourceParser, view) {

    override val view = super.view as JsDrawerLayout

}