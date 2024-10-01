package org.ys.game.core.automator.filter

import org.ys.game.core.accessibility.UiSelector
import org.ys.game.core.automator.UiObject
import java.util.*

open class Selector : Filter {

    val filters = LinkedList<Filter>()

    override fun filter(node: UiObject) = filters.all { it.filter(node) }

    fun add(filter: Filter) = filters.add(filter)

    fun append(uiSelector: UiSelector) = filters.addAll(uiSelector.selector.filters)

    override fun toString() = filters.joinToString(".").ifEmpty { Selector::class.java.toString() }

}