package org.ys.game.theme.widget

import android.content.Context
import android.graphics.PorterDuff
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import org.ys.game.theme.ThemeColor
import org.ys.game.theme.ThemeColorManager.add
import org.ys.game.theme.ThemeColorMutable

/**
 * Created by Stardust on May 10, 2017.
 * Transformed by SuperMonster003 on May 15, 2023.
 */
class ThemeColorImageViewCompat : AppCompatImageView, ThemeColorMutable {

    private var mColor = 0

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        add(this)
    }

    override fun setThemeColor(color: ThemeColor) {
        if (mColor != color.colorPrimary) {
            mColor = color.colorPrimary
            setColor(color.colorPrimary)
        }
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        if (mColor != 0) {
            setColor(mColor)
        }
    }

    private fun setColor(color: Int) {
        @Suppress("DEPRECATION")
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)
    }

}