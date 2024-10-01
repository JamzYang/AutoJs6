package org.ys.game.core.ui.inflater.inflaters

import android.content.Context
import android.view.ViewGroup
import android.widget.CheckedTextView
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.inflater.ViewCreator

open class CheckedTextViewInflater<V : CheckedTextView>(resourceParser: ResourceParser) : TextViewInflater<V>(resourceParser) {

    override fun getCreator(): ViewCreator<in V> = object : ViewCreator<CheckedTextView> {
        override fun create(context: Context, attrs: HashMap<String, String>, parent: ViewGroup?): CheckedTextView {
            return CheckedTextView(context)
        }
    }

}
