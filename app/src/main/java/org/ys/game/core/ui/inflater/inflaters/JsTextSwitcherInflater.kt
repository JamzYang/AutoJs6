package org.ys.game.core.ui.inflater.inflaters

import android.content.Context
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.inflater.ViewCreator
import org.ys.game.core.ui.widget.JsTextSwitcher
import org.ys.game.core.ui.widget.JsTextView
import org.ys.gamecat.R

class JsTextSwitcherInflater(resourceParser: ResourceParser) : TextSwitcherInflater<JsTextSwitcher>(resourceParser) {

    override fun getCreator(): ViewCreator<in JsTextSwitcher> = object : ViewCreator<JsTextSwitcher> {
        override fun create(context: Context, attrs: HashMap<String, String>, parent: ViewGroup?): JsTextSwitcher {
            return JsTextSwitcher(context)
        }
    }

}