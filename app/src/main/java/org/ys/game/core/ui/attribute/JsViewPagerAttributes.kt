package org.ys.game.core.ui.attribute

import android.view.View
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.widget.JsViewPager

class JsViewPagerAttributes(resourceParser: ResourceParser, view: View) : ViewPagerAttributes(resourceParser, view) {

    override val view = super.view as JsViewPager

    override fun onRegisterAttrs() {
        super.onRegisterAttrs()

        registerAttr("titles") {
            view.setTitles(parseAttrValue(it).toTypedArray())
            view.adapter?.notifyDataSetChanged()
        }
    }

}