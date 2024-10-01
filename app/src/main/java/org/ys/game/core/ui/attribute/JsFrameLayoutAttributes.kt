package org.ys.game.core.ui.attribute

import android.view.View
import android.widget.FrameLayout
import androidx.core.view.children
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.inflater.util.Gravities
import org.ys.game.core.ui.widget.JsFrameLayout

/**
 * Created by SuperMonster003 on May 20, 2023.
 */
class JsFrameLayoutAttributes(resourceParser: ResourceParser, view: View) : FrameLayoutAttributes(resourceParser, view) {

    override val view = super.view as JsFrameLayout

    override fun onRegisterAttrs() {
        super.onRegisterAttrs()

        registerAttr("gravity") {
            val gravity = Gravities.parse(it)
            view.children.forEach { child ->
                child.layoutParams = (child.layoutParams as FrameLayout.LayoutParams).apply {
                    this.gravity = gravity
                }
            }
        }
    }

}