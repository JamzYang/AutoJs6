package org.ys.game.core.ui.inflater.inflaters

import android.content.Context
import android.view.ViewGroup
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.inflater.ViewCreator
import org.ys.game.core.ui.widget.JsDatePicker

class JsDatePickerInflater(resourceParser: ResourceParser) : DatePickerInflater<JsDatePicker>(resourceParser) {

    override fun getCreator(): ViewCreator<in JsDatePicker> = object : ViewCreator<JsDatePicker> {
        override fun create(context: Context, attrs: HashMap<String, String>, parent: ViewGroup?): JsDatePicker {
            return JsDatePicker(context)
        }
    }

}