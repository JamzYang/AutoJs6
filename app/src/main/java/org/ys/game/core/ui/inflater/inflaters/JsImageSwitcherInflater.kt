package org.ys.game.core.ui.inflater.inflaters

import android.content.Context
import android.view.ViewGroup
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.inflater.ViewCreator
import org.ys.game.core.ui.widget.JsImageSwitcher

class JsImageSwitcherInflater(resourceParser: ResourceParser) : ImageSwitcherInflater<JsImageSwitcher>(resourceParser) {

    override fun getCreator(): ViewCreator<JsImageSwitcher> = object : ViewCreator<JsImageSwitcher> {
        override fun create(context: Context, attrs: HashMap<String, String>, parent: ViewGroup?): JsImageSwitcher {
            return JsImageSwitcher(context)
        }
    }

}