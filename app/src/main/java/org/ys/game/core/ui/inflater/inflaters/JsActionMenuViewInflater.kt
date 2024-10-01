package org.ys.game.core.ui.inflater.inflaters

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.inflater.ViewCreator
import org.ys.game.core.ui.widget.JsActionMenuView
import org.ys.gamecat.R
import org.ys.gamecat.databinding.JsToolbarBinding


class JsActionMenuViewInflater(resourceParser: ResourceParser) : ActionMenuViewInflater<JsActionMenuView>(resourceParser) {

    override fun getCreator() = object : ViewCreator<JsActionMenuView> {
        override fun create(context: Context, attrs: HashMap<String, String>, parent: ViewGroup?): JsActionMenuView {
            val amv = View.inflate(context, R.layout.js_actionmenuview, null) as JsActionMenuView

            val toolbar = JsToolbarBinding.inflate(LayoutInflater.from(context)).toolbar

            // FIXME by SuperMonster003 on Jun 7, 2023.
            //  ! Doesn't work.
            @Suppress("DEPRECATION")
            toolbar.overflowIcon?.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)

            return amv
        }
    }

}