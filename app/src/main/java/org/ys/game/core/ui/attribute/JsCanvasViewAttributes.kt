package org.ys.game.core.ui.attribute

import android.view.View
import org.ys.game.core.ui.widget.JsCanvasView
import org.ys.game.core.ui.inflater.ResourceParser

class JsCanvasViewAttributes(resourceParser: ResourceParser, view: View) : TextureViewAttributes(resourceParser, view) {

    override val view = super.view as JsCanvasView

}