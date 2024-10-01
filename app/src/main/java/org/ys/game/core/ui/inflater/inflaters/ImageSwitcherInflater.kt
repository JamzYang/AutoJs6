package org.ys.game.core.ui.inflater.inflaters

import android.content.Context
import android.view.ViewGroup
import android.widget.ImageSwitcher
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.inflater.ViewCreator

open class ImageSwitcherInflater<V : ImageSwitcher>(resourceParser: ResourceParser) : ViewSwitcherInflater<V>(resourceParser) {

    override fun getCreator(): ViewCreator<in V> = object : ViewCreator<ImageSwitcher> {
        override fun create(context: Context, attrs: HashMap<String, String>, parent: ViewGroup?): ImageSwitcher {
            return ImageSwitcher(context)
        }
    }

}
