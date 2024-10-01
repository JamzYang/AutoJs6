package org.ys.game.permission

import android.content.Context
import org.ys.game.ui.main.drawer.CommandBasedPermissionItemHelper
import org.ys.game.ui.main.drawer.IPermissionItem.Companion.ACTION
import org.ys.game.util.SettingsUtils

class WriteSecureSettingsPermission(override val context: Context) : CommandBasedPermissionItemHelper {

    override fun has(): Boolean = SettingsUtils.SecureSettings.isGranted(context)

    override fun getCommand(action: ACTION): String {
        val actionString = when (action) {
            ACTION.REQUEST -> "grant"
            ACTION.REVOKE -> "revoke"
        }
        return "pm $actionString ${context.packageName} ${Base.WRITE_SECURE_SETTINGS_PERMISSION}"
    }

}