package org.ys.game.core.ui.attribute

import android.view.View
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.widget.JsRelativeLayout

class JsRelativeLayoutAttributes(resourceParser: ResourceParser, view: View) : RelativeLayoutAttributes(resourceParser, view) {

    override val view = super.view as JsRelativeLayout

}