package org.ys.game.core.ui.attribute

import android.view.View
import android.widget.RadioButton
import org.ys.game.core.ui.inflater.ResourceParser

/**
 * Created by SuperMonster003 on May 21, 2023.
 */
open class RadioButtonAttributes(resourceParser: ResourceParser, view: View) : CompoundButtonAttributes(resourceParser, view) {

    override val view = super.view as RadioButton

}