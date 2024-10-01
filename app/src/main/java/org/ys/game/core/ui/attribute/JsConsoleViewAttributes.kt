package org.ys.game.core.ui.attribute

import android.view.View
import org.ys.game.core.ui.widget.JsConsoleView
import org.ys.game.core.ui.inflater.ResourceParser

class JsConsoleViewAttributes(resourceParser: ResourceParser, view: View) : ConsoleViewAttributes(resourceParser, view) {

    override val view = super.view as JsConsoleView

}