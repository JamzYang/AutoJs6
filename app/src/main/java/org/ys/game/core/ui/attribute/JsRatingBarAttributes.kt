package org.ys.game.core.ui.attribute

import android.view.View
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.widget.JsRatingBar

class JsRatingBarAttributes(resourceParser: ResourceParser, view: View) : RatingBarAttributes(resourceParser, view) {

    override val view = super.view as JsRatingBar

}