package org.ys.game.core.ui.attribute

import android.view.View
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.widget.JsFloatingActionButton

class JsFloatingActionButtonAttributes(resourceParser: ResourceParser, view: View) : FloatingActionButtonAttributes(resourceParser, view) {

    override val view = super.view as JsFloatingActionButton

}