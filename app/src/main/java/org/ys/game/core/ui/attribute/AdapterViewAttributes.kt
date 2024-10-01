package org.ys.game.core.ui.attribute

import android.view.View
import android.widget.AdapterView
import org.ys.game.core.ui.inflater.ResourceParser

open class AdapterViewAttributes(resourceParser: ResourceParser, view: View) : ViewGroupAttributes(resourceParser, view) {

    override val view = super.view as AdapterView<*>

    override fun onRegisterAttrs() {
        super.onRegisterAttrs()

        registerAttr("selection") { view.setSelection(it.toInt()) }
    }

}
