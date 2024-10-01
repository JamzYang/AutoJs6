package org.ys.game.core.ui.inflater.inflaters

import android.content.Context
import android.view.ViewGroup
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.inflater.ViewCreator
import org.ys.game.core.ui.widget.JsCalendarView

class JsCalendarViewInflater(resourceParser: ResourceParser) : CalendarViewInflater<JsCalendarView>(resourceParser) {

    override fun getCreator(): ViewCreator<in JsCalendarView> = object : ViewCreator<JsCalendarView> {
        override fun create(context: Context, attrs: HashMap<String, String>, parent: ViewGroup?): JsCalendarView {
            return JsCalendarView(context)
        }
    }

}