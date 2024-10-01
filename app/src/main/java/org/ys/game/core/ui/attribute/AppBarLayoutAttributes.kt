package org.ys.game.core.ui.attribute

import android.view.View
import com.google.android.material.appbar.AppBarLayout
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.inflater.util.Dimensions
import org.ys.game.util.ColorUtils

open class AppBarLayoutAttributes(resourceParser: ResourceParser, view: View) : LinearLayoutAttributes(resourceParser, view) {

    override val view = super.view as AppBarLayout

    override fun onRegisterAttrs() {
        super.onRegisterAttrs()

        registerAttr("expanded") { view.setExpanded(it.toBoolean()) }
        registerAttrs(arrayOf("isLifted", "lifted")) { view.isLifted = it.toBoolean() }
        registerAttrs(arrayOf("statusBarForegroundColor", "statusBarFgColor")) { view.setStatusBarForegroundColor(ColorUtils.parse(view, it)) }
        @Suppress("DEPRECATION")
        registerAttr("elevation") { view.targetElevation = Dimensions.parseToPixel(it, view) }
    }

}