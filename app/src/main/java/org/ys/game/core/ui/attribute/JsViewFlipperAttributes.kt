package org.ys.game.core.ui.attribute

import android.view.View
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.widget.JsViewFlipper

class JsViewFlipperAttributes(resourceParser: ResourceParser, view: View) : ViewFlipperAttributes(resourceParser, view) {

    override val view = super.view as JsViewFlipper

}