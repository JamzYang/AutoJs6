package org.ys.game.core.ui.inflater.inflaters

import android.content.Context
import android.view.ViewGroup
import android.widget.RadioButton
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.inflater.ViewCreator

/**
 * Created by SuperMonster003 on May 21, 2023.
 */
open class RadioButtonInflater(resourceParser: ResourceParser) : CompoundButtonInflater<RadioButton>(resourceParser) {

    override fun getCreator(): ViewCreator<in RadioButton> = object : ViewCreator<RadioButton> {
        override fun create(context: Context, attrs: HashMap<String, String>, parent: ViewGroup?): RadioButton {
            return RadioButton(context)
        }
    }

}