package org.ys.game.core.ui.inflater.inflaters

import android.content.Context
import android.view.ViewGroup
import android.widget.CalendarView
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.inflater.ViewCreator

open class CalendarViewInflater<V : CalendarView>(resourceParser: ResourceParser) : FrameLayoutInflater<V>(resourceParser) {

    override fun getCreator(): ViewCreator<in V> = object : ViewCreator<CalendarView> {
        override fun create(context: Context, attrs: HashMap<String, String>, parent: ViewGroup?): CalendarView {
            return CalendarView(context)
        }
    }

}
