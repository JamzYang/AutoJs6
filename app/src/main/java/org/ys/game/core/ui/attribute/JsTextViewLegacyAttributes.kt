package org.ys.game.core.ui.attribute

import android.view.View
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.widget.JsTextViewLegacy

class JsTextViewLegacyAttributes(resourceParser: ResourceParser, view: View) : AppCompatTextViewLegacyAttributes(resourceParser, view) {

    override val view = super.view as JsTextViewLegacy

}