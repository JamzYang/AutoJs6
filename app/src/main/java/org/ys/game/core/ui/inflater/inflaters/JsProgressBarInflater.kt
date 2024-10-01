package org.ys.game.core.ui.inflater.inflaters

import android.content.Context
import android.view.ViewGroup
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.inflater.ViewCreator
import org.ys.game.core.ui.widget.JsProgressBar

class JsProgressBarInflater(resourceParser: ResourceParser) : ProgressBarInflater<JsProgressBar>(resourceParser) {

    override fun getCreator(): ViewCreator<in JsProgressBar> = object : ViewCreator<JsProgressBar> {
        override fun create(context: Context, attrs: HashMap<String, String>, parent: ViewGroup?): JsProgressBar {
            return JsProgressBar(context)
        }
    }

}