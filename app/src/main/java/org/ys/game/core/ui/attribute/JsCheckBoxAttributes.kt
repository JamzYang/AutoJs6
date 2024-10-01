package org.ys.game.core.ui.attribute

import android.view.View
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.widget.JsCheckBox

class JsCheckBoxAttributes(resourceParser: ResourceParser, view: View) : AppCompatCheckBoxAttributes(resourceParser, view) {

    override val view = super.view as JsCheckBox

}