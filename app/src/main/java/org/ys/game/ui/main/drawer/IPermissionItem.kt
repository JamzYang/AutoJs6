package org.ys.game.ui.main.drawer

interface IPermissionItem {

    fun getCommand(action: ACTION): String

    companion object {
        enum class ACTION { REQUEST, REVOKE }
    }

}
