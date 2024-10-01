package org.ys.game.core.ui.attribute

import android.view.View
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.widget.JsRadioGroup

class JsRadioGroupAttributes(resourceParser: ResourceParser, view: View) : RadioGroupAttributes(resourceParser, view) {

    override val view = super.view as JsRadioGroup

}