package org.ys.game.core.ui.inflater.inflaters

import android.content.Context
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.inflater.ViewCreator

open class HorizontalScrollViewInflater<V : HorizontalScrollView>(resourceParser: ResourceParser) : FrameLayoutInflater<V>(resourceParser) {

    override fun getCreator(): ViewCreator<in V> = object : ViewCreator<HorizontalScrollView> {
        override fun create(context: Context, attrs: HashMap<String, String>, parent: ViewGroup?): HorizontalScrollView {
            return HorizontalScrollView(context)
        }
    }

}
