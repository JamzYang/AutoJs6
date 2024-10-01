package org.ys.game.ui.main.drawer

interface IPermissionAdbItem : IPermissionItem {

    fun requestWithAdb(): Boolean

    fun revokeWithAdb(): Boolean

}
