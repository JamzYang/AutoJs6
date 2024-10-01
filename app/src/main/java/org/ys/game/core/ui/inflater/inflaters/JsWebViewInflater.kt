package org.ys.game.core.ui.inflater.inflaters

import android.content.Context
import android.view.ViewGroup
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.inflater.ViewCreator
import org.ys.game.core.ui.widget.JsWebView

/**
 * Created by SuperMonster003 on May 15, 2023.
 * Transformed by SuperMonster003 on May 20, 2023.
 */
class JsWebViewInflater(resourceParser: ResourceParser) : WebViewInflater<JsWebView>(resourceParser) {

    override fun getCreator(): ViewCreator<in JsWebView> = object : ViewCreator<JsWebView> {
        override fun create(context: Context, attrs: HashMap<String, String>, parent: ViewGroup?): JsWebView {
            return JsWebView(context)
        }
    }

}