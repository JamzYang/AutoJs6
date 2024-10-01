package org.ys.game.core.ui.attribute

import android.view.View
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.widget.JsCalendarView

class JsCalendarViewAttributes(resourceParser: ResourceParser, view: View) : CalendarViewAttributes(resourceParser, view) {

    override val view = super.view as JsCalendarView

}