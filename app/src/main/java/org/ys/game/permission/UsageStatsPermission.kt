package org.ys.game.permission

import android.content.Context
import org.ys.game.app.AppOps.isUsageStatsPermissionGranted
import org.ys.game.ui.main.drawer.PermissionItemHelper
import org.ys.game.util.IntentUtils

class UsageStatsPermission(override val context: Context) : PermissionItemHelper {

    override fun has() = isUsageStatsPermissionGranted(context)

    override fun request() = false.also { config() }

    override fun revoke() = false.also { config() }

    fun config() = IntentUtils.requestAppUsagePermission(context)

}