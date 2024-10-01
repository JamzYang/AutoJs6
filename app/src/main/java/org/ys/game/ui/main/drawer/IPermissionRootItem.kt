package org.ys.game.ui.main.drawer

interface IPermissionRootItem : IPermissionItem {

    fun requestWithRoot(): Boolean

    fun revokeWithRoot(): Boolean

}
