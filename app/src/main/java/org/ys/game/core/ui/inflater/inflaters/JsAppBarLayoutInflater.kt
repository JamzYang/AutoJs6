package org.ys.game.core.ui.inflater.inflaters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.inflater.ViewCreator
import org.ys.game.core.ui.widget.JsAppBarLayout
import org.ys.gamecat.R

class JsAppBarLayoutInflater(resourceParser: ResourceParser) : AppBarLayoutInflater<JsAppBarLayout>(resourceParser) {

    override fun getCreator() = object : ViewCreator<JsAppBarLayout> {
        override fun create(context: Context, attrs: HashMap<String, String>, parent: ViewGroup?): JsAppBarLayout {
            return View.inflate(context, R.layout.js_appbar, null) as JsAppBarLayout
        }
    }

}