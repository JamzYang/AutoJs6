package org.ys.game.core.ui.inflater.inflaters

import android.content.Context
import android.view.ViewGroup
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.inflater.ViewCreator
import org.ys.game.core.ui.widget.JsFloatingActionButton

class JsFloatingActionButtonInflater(resourceParser: ResourceParser) : FloatingActionButtonInflater<JsFloatingActionButton>(resourceParser) {

    override fun getCreator(): ViewCreator<in JsFloatingActionButton> = object : ViewCreator<JsFloatingActionButton> {
        override fun create(context: Context, attrs: HashMap<String, String>, parent: ViewGroup?): JsFloatingActionButton {
            return JsFloatingActionButton(context)
        }
    }

}