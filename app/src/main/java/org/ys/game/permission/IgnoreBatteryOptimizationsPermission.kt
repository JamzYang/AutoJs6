package org.ys.game.permission

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.os.Build
import android.provider.Settings
import org.ys.game.ui.main.drawer.PermissionItemHelper
import org.ys.game.util.ViewUtils
import org.ys.gamecat.R

/**
 * Created by SuperMonster003 on Jun 21, 2022.
 */
class IgnoreBatteryOptimizationsPermission(override val context: Context) : PermissionItemHelper {

    override fun has(): Boolean {
        return (context.getSystemService(Context.POWER_SERVICE) as PowerManager).isIgnoringBatteryOptimizations(context.packageName)
    }

    @SuppressLint("BatteryLife")
    override fun request() = tryStartActivity(
        Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:" + context.packageName))
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    )

    override fun revoke() = tryStartActivity(
        Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    )

    private fun tryStartActivity(i: Intent): Boolean {
        return try {
            context.startActivity(i)
            true
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            ViewUtils.showToast(context, R.string.text_failed)
            false
        }
    }
    companion object {
        fun isGranted(context: Context): Boolean {
            val packageName = context.packageName
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            return powerManager.isIgnoringBatteryOptimizations(packageName)
        }
    }
}
