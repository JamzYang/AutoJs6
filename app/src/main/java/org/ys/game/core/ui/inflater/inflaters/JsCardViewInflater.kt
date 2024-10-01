package org.ys.game.core.ui.inflater.inflaters

import android.content.Context
import android.view.ViewGroup
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.inflater.ViewCreator
import org.ys.game.core.ui.widget.JsCardView

class JsCardViewInflater(resourceParser: ResourceParser) : CardViewInflater<JsCardView>(resourceParser) {

    override fun getCreator(): ViewCreator<in JsCardView> = object : ViewCreator<JsCardView> {
        override fun create(context: Context, attrs: HashMap<String, String>, parent: ViewGroup?): JsCardView {
            return JsCardView(context)
        }
    }

}