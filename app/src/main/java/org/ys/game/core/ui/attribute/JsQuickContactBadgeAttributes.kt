package org.ys.game.core.ui.attribute

import android.view.View
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.widget.JsQuickContactBadge

class JsQuickContactBadgeAttributes(resourceParser: ResourceParser, view: View) : QuickContactBadgeAttributes(resourceParser, view) {

    override val view = super.view as JsQuickContactBadge

}