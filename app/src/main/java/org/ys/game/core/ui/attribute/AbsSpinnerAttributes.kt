package org.ys.game.core.ui.attribute

import android.view.View
import android.widget.AbsSpinner
import org.ys.game.core.ui.inflater.ResourceParser

open class AbsSpinnerAttributes(resourceParser: ResourceParser, view: View) : AdapterViewAttributes(resourceParser, view) {

    override val view = super.view as AbsSpinner

}
