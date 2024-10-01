package org.ys.game.core.ui.inflater.inflaters

import android.content.Context
import android.view.ViewGroup
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.inflater.ViewCreator
import org.ys.game.core.ui.widget.JsLinearLayout

class JsLinearLayoutInflater(resourceParser: ResourceParser) : LinearLayoutInflater<JsLinearLayout>(resourceParser) {

    override fun getCreator(): ViewCreator<in JsLinearLayout> = object : ViewCreator<JsLinearLayout> {
        override fun create(context: Context, attrs: HashMap<String, String>, parent: ViewGroup?): JsLinearLayout {
            return JsLinearLayout(context)
        }
    }

}