package org.ys.game.core.ui.attribute

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.ys.game.core.ui.inflater.ResourceParser

open class RecyclerViewAttributes(resourceParser: ResourceParser, view: View) : ViewGroupAttributes(resourceParser, view) {

    override val view = super.view as RecyclerView

}
