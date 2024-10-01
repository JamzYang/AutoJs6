package org.ys.game.core.ui.attribute

import android.view.View
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.widget.JsViewSwitcher

class JsViewSwitcherAttributes(resourceParser: ResourceParser, view: View) : ViewSwitcherAttributes(resourceParser, view) {

    override val view = super.view as JsViewSwitcher

}