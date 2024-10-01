package org.ys.game.core.ui.attribute

import android.view.View
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.widget.JsTextView

class JsTextViewAttributes(resourceParser: ResourceParser, view: View) : AppCompatTextViewAttributes(resourceParser, view) {

    override val view = super.view as JsTextView

}