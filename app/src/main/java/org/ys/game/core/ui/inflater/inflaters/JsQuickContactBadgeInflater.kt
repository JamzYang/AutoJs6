package org.ys.game.core.ui.inflater.inflaters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.inflater.ViewCreator
import org.ys.game.core.ui.widget.JsQuickContactBadge
import org.ys.gamecat.R

class JsQuickContactBadgeInflater(resourceParser: ResourceParser) : QuickContactBadgeInflater<JsQuickContactBadge>(resourceParser) {

    override fun getCreator(): ViewCreator<in JsQuickContactBadge> = object : ViewCreator<JsQuickContactBadge> {
        override fun create(context: Context, attrs: HashMap<String, String>, parent: ViewGroup?): JsQuickContactBadge {
            return (View.inflate(context, R.layout.js_quickcontactbadge, null) as JsQuickContactBadge).apply {
                setImageToDefault()
            }
        }
    }

}