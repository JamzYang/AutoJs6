package org.ys.game.util

import android.content.Context
import org.ys.game.app.GlobalAppContext
import org.ys.game.ui.log.LogActivity

object ConsoleUtils {

    @JvmStatic
    @JvmOverloads
    fun launch(context: Context? = GlobalAppContext.get()) = try {
        context?.let { LogActivity.launch(it) }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

}