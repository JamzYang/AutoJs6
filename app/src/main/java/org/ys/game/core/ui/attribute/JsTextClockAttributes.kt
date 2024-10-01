package org.ys.game.core.ui.attribute

import android.view.View
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.widget.JsTextClock

/**
 * Created by SuperMonster003 on May 23, 2023.
 */
class JsTextClockAttributes(resourceParser: ResourceParser, view: View) : TextClockAttributes(resourceParser, view) {

    override val view = super.view as JsTextClock

}