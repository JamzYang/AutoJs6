package org.ys.game.core.ui.attribute

import android.view.View
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.widget.JsAppBarLayout

class JsAppBarLayoutAttributes(resourceParser: ResourceParser, view: View) : AppBarLayoutAttributes(resourceParser, view) {

    override val view = super.view as JsAppBarLayout

}