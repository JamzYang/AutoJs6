package org.ys.game.core.ui.inflater.inflaters

import android.content.Context
import android.view.ViewGroup
import android.widget.RadioButton
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.inflater.ViewCreator
import org.ys.game.core.ui.widget.JsRadioButton

class JsRadioButtonInflater(resourceParser: ResourceParser) : RadioButtonInflater(resourceParser) {

    override fun getCreator(): ViewCreator<in RadioButton> = object : ViewCreator<RadioButton> {
        override fun create(context: Context, attrs: HashMap<String, String>, parent: ViewGroup?): JsRadioButton {
            return JsRadioButton(context)
        }
    }

}