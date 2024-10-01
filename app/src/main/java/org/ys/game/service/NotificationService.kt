package org.ys.game.service

import android.content.Context
import android.content.Intent
import android.provider.Settings
import org.ys.game.core.notification.NotificationListenerService
import org.ys.game.ui.main.drawer.ServiceItemHelper

class NotificationService(override val context: Context) : ServiceItemHelper {

    override val isRunning: Boolean
        get() = NotificationListenerService.instance != null

    override fun start(): Boolean = false.also { config() }

    override fun stop(): Boolean = false.also { config() }

    fun config() {
        context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
    }

}