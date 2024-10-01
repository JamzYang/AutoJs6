package org.ys.game.core.ui.inflater.inflaters

import android.content.Context
import android.view.ViewGroup
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.inflater.ViewCreator
import org.ys.game.core.ui.widget.JsRadioGroup

class JsRadioGroupInflater(resourceParser: ResourceParser) : RadioGroupInflater<JsRadioGroup>(resourceParser) {

    override fun getCreator(): ViewCreator<in JsRadioGroup> = object : ViewCreator<JsRadioGroup> {
        override fun create(context: Context, attrs: HashMap<String, String>, parent: ViewGroup?): JsRadioGroup {
            return JsRadioGroup(context)
        }
    }

}