package org.ys.game.core.ui.attribute

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import org.ys.game.core.ui.inflater.ResourceParser

open class AppCompatTextViewAttributes(resourceParser: ResourceParser, view: View): TextViewAttributes(resourceParser, view) {

    override val view = super.view as AppCompatTextView

}
