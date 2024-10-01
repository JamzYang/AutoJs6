package org.ys.game.core.ui.inflater.inflaters

import android.content.Context
import android.view.ViewGroup
import android.widget.EditText
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.inflater.ViewCreator

open class EditTextInflater<V : EditText>(resourceParser: ResourceParser) : TextViewInflater<V>(resourceParser) {

    override fun getCreator(): ViewCreator<in V> = object : ViewCreator<EditText> {
        override fun create(context: Context, attrs: HashMap<String, String>, parent: ViewGroup?): EditText {
            return EditText(context)
        }
    }

}
