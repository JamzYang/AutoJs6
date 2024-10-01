package org.ys.game.core.ui.attribute

import android.view.View
import android.widget.CheckBox
import org.ys.game.core.ui.inflater.ResourceParser

open class CheckBoxAttributes(resourceParser: ResourceParser, view: View) : CompoundButtonAttributes(resourceParser, view) {

    override val view = super.view as CheckBox

}
