package org.ys.game.core.ui.attribute

import android.view.View
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.widget.JsLinearLayout

/**
 * Created by SuperMonster003 on May 20, 2023.
 */
class JsLinearLayoutAttributes(resourceParser: ResourceParser, view: View) : LinearLayoutAttributes(resourceParser, view) {

    override val view = super.view as JsLinearLayout

}