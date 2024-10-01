package org.ys.game.core.ui.inflater.inflaters

import android.content.Context
import android.view.ViewGroup
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.inflater.ViewCreator
import org.ys.game.core.ui.widget.JsRelativeLayout

class JsRelativeLayoutInflater(resourceParser: ResourceParser) : RelativeLayoutInflater<JsRelativeLayout>(resourceParser) {

    override fun getCreator(): ViewCreator<in JsRelativeLayout> = object : ViewCreator<JsRelativeLayout> {
        override fun create(context: Context, attrs: HashMap<String, String>, parent: ViewGroup?): JsRelativeLayout {
            return JsRelativeLayout(context)
        }
    }

}