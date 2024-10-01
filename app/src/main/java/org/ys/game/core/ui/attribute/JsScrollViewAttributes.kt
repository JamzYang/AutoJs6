package org.ys.game.core.ui.attribute

import android.view.View
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.widget.JsScrollView

class JsScrollViewAttributes(resourceParser: ResourceParser, view: View) : ScrollViewAttributes(resourceParser, view) {

    override val view = super.view as JsScrollView

}