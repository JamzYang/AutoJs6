package org.ys.game.core.ui.attribute

import android.view.View
import android.widget.CheckedTextView
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.util.ColorUtils

open class CheckedTextViewAttributes(resourceParser: ResourceParser, view: View) : TextViewAttributes(resourceParser, view) {

    override val view = super.view as CheckedTextView

    override fun onRegisterAttrs() {
        super.onRegisterAttrs()

        registerAttr("checkMarkDrawable") { view.checkMarkDrawable = drawables.parse(view, it) }
        registerAttrs(arrayOf("checkMarkTintList", "checkMarkTint")) { view.checkMarkTintList = ColorUtils.toColorStateList(view, it) }
    }

}
