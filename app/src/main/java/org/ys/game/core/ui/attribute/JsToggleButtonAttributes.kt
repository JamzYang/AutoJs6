package org.ys.game.core.ui.attribute

import android.view.View
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.widget.JsToggleButton

class JsToggleButtonAttributes(resourceParser: ResourceParser, view: View) : ToggleButtonAttributes(resourceParser, view) {

    override val view = super.view as JsToggleButton

}