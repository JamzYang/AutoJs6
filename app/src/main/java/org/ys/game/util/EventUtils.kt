package org.ys.game.util

import android.view.KeyEvent

object EventUtils {

    private fun isKeyBack(e: KeyEvent) = e.keyCode == KeyEvent.KEYCODE_BACK

    private fun isActionUp(e: KeyEvent) = e.action == KeyEvent.ACTION_UP

    fun isKeyBackAndActionUp(e: KeyEvent) = isKeyBack(e) && isActionUp(e)

}