package org.ys.game.core.automator.filter

import org.ys.game.core.automator.UiObject

/**
 * Created by Stardust on Mar 9, 2017.
 */
interface Filter {

    fun filter(node: UiObject): Boolean

}
