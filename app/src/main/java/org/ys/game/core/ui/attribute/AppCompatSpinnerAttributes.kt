package org.ys.game.core.ui.attribute

import android.view.View
import androidx.appcompat.widget.AppCompatSpinner
import org.ys.game.core.ui.inflater.ResourceParser

open class AppCompatSpinnerAttributes(resourceParser: ResourceParser, view: View) : SpinnerAttributes(resourceParser, view) {

    override val view = super.view as AppCompatSpinner

}
