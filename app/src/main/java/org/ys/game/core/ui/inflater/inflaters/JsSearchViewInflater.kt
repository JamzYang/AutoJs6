package org.ys.game.core.ui.inflater.inflaters

import android.content.Context
import android.view.ViewGroup
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.inflater.ViewCreator
import org.ys.game.core.ui.widget.JsSearchView

class JsSearchViewInflater(resourceParser: ResourceParser) : SearchViewInflater<JsSearchView>(resourceParser) {

    override fun getCreator(): ViewCreator<in JsSearchView> = object : ViewCreator<JsSearchView> {
        override fun create(context: Context, attrs: HashMap<String, String>, parent: ViewGroup?): JsSearchView {
            return JsSearchView(context)
        }
    }

}