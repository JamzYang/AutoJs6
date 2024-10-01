package org.ys.game.core.ui.inflater.inflaters

import android.content.Context
import android.view.ViewGroup
import android.widget.ViewAnimator
import android.widget.ViewSwitcher
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.inflater.ViewCreator

open class ViewSwitcherInflater<V : ViewAnimator>(resourceParser: ResourceParser) : ViewAnimatorInflater<V>(resourceParser) {

    override fun getCreator(): ViewCreator<in V> = object : ViewCreator<ViewAnimator> {
        override fun create(context: Context, attrs: HashMap<String, String>, parent: ViewGroup?): ViewSwitcher {
            return ViewSwitcher(context)
        }
    }

}
