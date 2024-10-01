package org.ys.game.core.ui.attribute

import android.view.View
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.widget.JsDatePicker

class JsDatePickerAttributes(resourceParser: ResourceParser, view: View) : DatePickerAttributes(resourceParser, view) {

    override val view = super.view as JsDatePicker

}