package org.ys.game.service

import android.content.Context
import org.ys.game.external.foreground.MainActivityForegroundService
import org.ys.game.ui.main.drawer.ServiceItemHelper
import org.ys.game.util.ForegroundServiceUtils

class ForegroundService(override val context: Context) : ServiceItemHelper {

    private val mClassName = MainActivityForegroundService::class.java

    override val isRunning
        get() = ForegroundServiceUtils.isRunning(context, mClassName)

    override fun start(): Boolean {
        return ForegroundServiceUtils.startService(context, mClassName)
    }

    override fun stop(): Boolean {
        return ForegroundServiceUtils.stopServiceIfNeeded(context, mClassName)
    }

}