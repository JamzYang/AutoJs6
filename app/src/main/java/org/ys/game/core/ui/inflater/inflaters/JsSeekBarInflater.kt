package org.ys.game.core.ui.inflater.inflaters

import android.content.Context
import android.view.ViewGroup
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.inflater.ViewCreator
import org.ys.game.core.ui.widget.JsSeekBar

class JsSeekBarInflater(resourceParser: ResourceParser) : SeekBarInflater<JsSeekBar>(resourceParser) {

    override fun getCreator(): ViewCreator<in JsSeekBar> = object : ViewCreator<JsSeekBar> {
        override fun create(context: Context, attrs: HashMap<String, String>, parent: ViewGroup?): JsSeekBar {
            return JsSeekBar(context)
        }
    }

}