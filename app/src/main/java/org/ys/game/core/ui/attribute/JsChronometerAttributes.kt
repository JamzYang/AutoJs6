package org.ys.game.core.ui.attribute

import android.view.View
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.widget.JsChronometer

class JsChronometerAttributes(resourceParser: ResourceParser, view: View) : ChronometerAttributes(resourceParser, view) {

    override val view = super.view as JsChronometer

}