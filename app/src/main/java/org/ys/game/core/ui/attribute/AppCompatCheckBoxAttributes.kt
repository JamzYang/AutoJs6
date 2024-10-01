package org.ys.game.core.ui.attribute

import android.view.View
import androidx.appcompat.widget.AppCompatCheckBox
import org.ys.game.core.ui.inflater.ResourceParser

open class AppCompatCheckBoxAttributes(resourceParser: ResourceParser, view: View) : CheckBoxAttributes(resourceParser, view) {

    override val view = super.view as AppCompatCheckBox

}
