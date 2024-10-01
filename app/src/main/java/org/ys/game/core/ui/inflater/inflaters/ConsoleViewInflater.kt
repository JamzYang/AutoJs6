package org.ys.game.core.ui.inflater.inflaters

import android.content.Context
import android.view.ViewGroup
import org.ys.game.core.console.ConsoleView
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.inflater.ViewCreator

open class ConsoleViewInflater<V: ConsoleView>(resourceParser: ResourceParser): FrameLayoutInflater<V>(resourceParser) {

    override fun getCreator(): ViewCreator<in V> = object : ViewCreator<ConsoleView> {
        override fun create(context: Context, attrs: HashMap<String, String>, parent: ViewGroup?): ConsoleView {
            return ConsoleView(context)
        }
    }

}
