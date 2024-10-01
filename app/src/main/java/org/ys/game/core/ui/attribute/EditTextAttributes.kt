package org.ys.game.core.ui.attribute

import android.view.View
import android.widget.EditText
import org.ys.game.core.ui.inflater.ResourceParser

open class EditTextAttributes(resourceParser: ResourceParser, view: View) : TextViewAttributes(resourceParser, view) {

    override val view = super.view as EditText

}
