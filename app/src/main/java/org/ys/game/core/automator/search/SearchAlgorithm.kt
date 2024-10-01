package org.ys.game.core.automator.search

import org.ys.game.core.automator.UiObject
import org.ys.game.core.automator.filter.Filter

interface SearchAlgorithm {

    fun search(root: UiObject, filter: Filter, limit: Int = Int.MAX_VALUE): ArrayList<UiObject>

}