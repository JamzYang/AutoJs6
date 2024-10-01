package org.ys.game.core.ui.attribute

import android.view.View
import android.widget.FrameLayout
import org.ys.game.core.ui.inflater.ResourceParser

open class FrameLayoutAttributes(resourceParser: ResourceParser, view: View) : ViewGroupAttributes(resourceParser, view) {

    override val view = super.view as FrameLayout

}
