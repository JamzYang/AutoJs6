package org.ys.game.core.ui.attribute

import android.view.View
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.widget.JsImageButton

class JsImageButtonAttributes(resourceParser: ResourceParser, view: View) : ImageButtonAttributes(resourceParser, view) {

    override val view = super.view as JsImageButton

}