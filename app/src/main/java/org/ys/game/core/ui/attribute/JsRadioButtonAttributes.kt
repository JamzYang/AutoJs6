package org.ys.game.core.ui.attribute

import android.view.View
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.widget.JsRadioButton

class JsRadioButtonAttributes(resourceParser: ResourceParser, view: View) : RadioButtonAttributes(resourceParser, view) {

    override val view = super.view as JsRadioButton

}