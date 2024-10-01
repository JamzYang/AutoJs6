package org.ys.game.core.ui.attribute

import android.view.View
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.widget.JsProgressBar

class JsProgressBarAttributes(resourceParser: ResourceParser, view: View) : ProgressBarAttributes(resourceParser, view) {

    override val view = super.view as JsProgressBar

}