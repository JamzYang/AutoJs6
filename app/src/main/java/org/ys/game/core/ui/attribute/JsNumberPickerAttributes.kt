package org.ys.game.core.ui.attribute

import android.view.View
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.widget.JsNumberPicker

open class JsNumberPickerAttributes(resourceParser: ResourceParser, view: View) : NumberPickerAttributes(resourceParser, view) {

    override val view = super.view as JsNumberPicker

}