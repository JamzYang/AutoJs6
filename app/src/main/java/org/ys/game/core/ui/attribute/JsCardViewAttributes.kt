package org.ys.game.core.ui.attribute

import android.view.View
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.widget.JsCardView

class JsCardViewAttributes(resourceParser: ResourceParser, view: View) : CardViewAttributes(resourceParser, view) {

    override val view = super.view as JsCardView

}