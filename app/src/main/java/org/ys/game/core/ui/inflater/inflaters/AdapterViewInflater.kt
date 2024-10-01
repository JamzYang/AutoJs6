package org.ys.game.core.ui.inflater.inflaters

import android.widget.AdapterView
import org.ys.game.core.ui.inflater.ResourceParser

open class AdapterViewInflater<V : AdapterView<*>>(resourceParser: ResourceParser) : ViewGroupInflater<V>(resourceParser) {
    // Empty inflater.
}
