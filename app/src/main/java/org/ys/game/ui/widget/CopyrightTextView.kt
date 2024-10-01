package org.ys.game.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import org.ys.gamecat.R
import java.util.Calendar

class CopyrightTextView : TextView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        text = context.getString(R.string.text_copyright_dynamic, Calendar.getInstance().get(Calendar.YEAR))
    }

}