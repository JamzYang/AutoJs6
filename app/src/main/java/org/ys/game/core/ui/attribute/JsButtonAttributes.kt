package org.ys.game.core.ui.attribute

import android.view.View
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.widget.JsButton

class JsButtonAttributes(resourceParser: ResourceParser, view: View) : ButtonAttributes(resourceParser, view) {

    override val view = super.view as JsButton

}