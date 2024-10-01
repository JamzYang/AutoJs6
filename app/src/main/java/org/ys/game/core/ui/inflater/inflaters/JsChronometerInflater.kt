package org.ys.game.core.ui.inflater.inflaters

import android.content.Context
import android.view.ViewGroup
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.inflater.ViewCreator
import org.ys.game.core.ui.widget.JsChronometer

class JsChronometerInflater(resourceParser: ResourceParser) : ChronometerInflater<JsChronometer>(resourceParser) {

    override fun getCreator(): ViewCreator<in JsChronometer> = object : ViewCreator<JsChronometer> {
        override fun create(context: Context, attrs: HashMap<String, String>, parent: ViewGroup?): JsChronometer {
            return JsChronometer(context)
        }
    }

}