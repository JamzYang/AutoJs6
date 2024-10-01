package org.ys.game.core.ui.inflater.inflaters

import android.content.Context
import android.view.ViewGroup
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.inflater.ViewCreator
import org.ys.game.core.ui.widget.JsTimePicker

class JsTimePickerInflater(resourceParser: ResourceParser) : TimePickerInflater<JsTimePicker>(resourceParser) {

    override fun getCreator(): ViewCreator<in JsTimePicker> = object : ViewCreator<JsTimePicker> {
        override fun create(context: Context, attrs: HashMap<String, String>, parent: ViewGroup?): JsTimePicker {
            return JsTimePicker(context)
        }
    }

}