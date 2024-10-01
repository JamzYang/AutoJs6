package org.ys.game.core.ui.attribute

import android.view.View
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.widget.JsSwitch

class JsSwitchAttributes(resourceParser: ResourceParser, view: View) : SwitchCompatAttributes(resourceParser, view) {

    override val view = super.view as JsSwitch

}