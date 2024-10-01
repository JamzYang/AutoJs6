package org.ys.game.permission

import android.content.Context
import android.os.Build
import org.ys.game.pref.Pref
import org.ys.game.ui.main.MainActivity
import org.ys.game.ui.main.drawer.PermissionItemHelper
import org.ys.game.util.NotificationUtils
import org.ys.gamecat.R

/**
 * Created by SuperMonster003 on May 4, 2023.
 */
class PostNotificationPermission(override val context: Context) : PermissionItemHelper, AbleToUrge {

    override fun has() = NotificationUtils.isEnabled()

    override fun request(): Boolean = false.also { config() }

    override fun revoke(): Boolean = false.also { config() }

    private fun config() = NotificationUtils.launchSettings()

    override fun urge() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            (context as? MainActivity)
                ?.requestMultiplePermissionsLauncher
                ?.let { NotificationUtils.requestPermission(it) }
        }
    }

    override fun urgeIfNeeded() {
        if (!Pref.getBoolean(R.string.key_post_notification_permission_requested, false)) {
            if (!has()) {
                urge()
            }
        }
    }

}