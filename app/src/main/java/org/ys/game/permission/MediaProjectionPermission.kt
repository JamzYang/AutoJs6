package org.ys.game.permission

import android.content.Context
import org.ys.game.app.AppOps.isProjectMediaAccessGranted
import org.ys.game.permission.Base.PROJECT_MEDIA_PERMISSION
import org.ys.game.ui.main.drawer.CommandBasedPermissionItemHelper
import org.ys.game.ui.main.drawer.IPermissionItem.Companion.ACTION

class MediaProjectionPermission(override val context: Context) : CommandBasedPermissionItemHelper {

    override fun has() = isProjectMediaAccessGranted(context)

    override fun getCommand(action: ACTION): String {
        val actionString = when (action) {
            ACTION.REQUEST -> "allow"
            ACTION.REVOKE -> "ignore"
        }
        return "appops set ${context.packageName} $PROJECT_MEDIA_PERMISSION $actionString"
    }

}