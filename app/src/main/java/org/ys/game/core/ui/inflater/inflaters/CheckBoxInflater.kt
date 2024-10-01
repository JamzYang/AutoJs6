package org.ys.game.core.ui.inflater.inflaters

import android.content.Context
import android.view.ViewGroup
import android.widget.CheckBox
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.inflater.ViewCreator

open class CheckBoxInflater<V: CheckBox>(resourceParser: ResourceParser): CompoundButtonInflater<V>(resourceParser) {

    override fun getCreator(): ViewCreator<in V> = object : ViewCreator<CheckBox> {
        override fun create(context: Context, attrs: HashMap<String, String>, parent: ViewGroup?): CheckBox {
            return CheckBox(context)
        }
    }

}
