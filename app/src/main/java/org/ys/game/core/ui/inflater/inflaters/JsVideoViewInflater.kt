package org.ys.game.core.ui.inflater.inflaters

import android.content.Context
import android.view.ViewGroup
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.inflater.ViewCreator
import org.ys.game.core.ui.widget.JsVideoView

class JsVideoViewInflater(resourceParser: ResourceParser) : VideoViewInflater<JsVideoView>(resourceParser) {

    override fun getCreator(): ViewCreator<in JsVideoView> = object : ViewCreator<JsVideoView> {
        override fun create(context: Context, attrs: HashMap<String, String>, parent: ViewGroup?): JsVideoView {
            return JsVideoView(context)
        }
    }

}