package org.ys.game.core.ui.inflater.inflaters

import android.content.Context
import android.view.ViewGroup
import android.widget.RelativeLayout
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.inflater.ViewCreator

open class RelativeLayoutInflater<V : RelativeLayout>(resourceParser: ResourceParser) : ViewGroupInflater<V>(resourceParser) {

    override fun getCreator(): ViewCreator<in V> = object : ViewCreator<RelativeLayout> {
        override fun create(context: Context, attrs: HashMap<String, String>, parent: ViewGroup?): RelativeLayout {
            return RelativeLayout(context)
        }
    }

}
