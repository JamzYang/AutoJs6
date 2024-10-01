package org.ys.game.core.ui.inflater.inflaters

import android.content.Context
import android.view.TextureView
import android.view.ViewGroup
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.inflater.ViewCreator

open class TextureViewInflater<V: TextureView>(resourceParser: ResourceParser): BaseViewInflater<V>(resourceParser) {

    override fun getCreator(): ViewCreator<in V> = object : ViewCreator<TextureView> {
        override fun create(context: Context, attrs: HashMap<String, String>, parent: ViewGroup?): TextureView {
            return TextureView(context)
        }
    }

}
