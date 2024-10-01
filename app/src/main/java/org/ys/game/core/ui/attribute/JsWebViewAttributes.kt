package org.ys.game.core.ui.attribute

import android.view.View
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.widget.JsWebView

/**
 * Created by SuperMonster003 on May 20, 2023.
 */
class JsWebViewAttributes(resourceParser: ResourceParser, view: View) : WebViewAttributes(resourceParser, view) {

    override val view = super.view as JsWebView

}