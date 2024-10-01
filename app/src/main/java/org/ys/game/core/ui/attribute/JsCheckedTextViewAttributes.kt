package org.ys.game.core.ui.attribute

import android.view.View
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.widget.JsCheckedTextView

class JsCheckedTextViewAttributes(resourceParser: ResourceParser, view: View) : CheckedTextViewAttributes(resourceParser, view) {

    override val view = super.view as JsCheckedTextView

}