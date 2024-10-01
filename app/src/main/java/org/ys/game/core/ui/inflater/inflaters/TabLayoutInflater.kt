package org.ys.game.core.ui.inflater.inflaters

import android.content.Context
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayout
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.inflater.ViewCreator

open class TabLayoutInflater<V: TabLayout>(resourceParser: ResourceParser): HorizontalScrollViewInflater<V>(resourceParser) {

    override fun getCreator(): ViewCreator<in V> = object : ViewCreator<TabLayout> {
        override fun create(context: Context, attrs: HashMap<String, String>, parent: ViewGroup?): TabLayout {
            return TabLayout(context)
        }
    }

}
