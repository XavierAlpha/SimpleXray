package com.simplexray.an.integration

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import com.simplexray.an.prefs.Preferences
import com.simplexray.an.service.TProxyService

class IntegrationReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_START_CORE = "com.simplexray.an.ACTION_START_CORE"
        const val ACTION_STOP_CORE  = "com.simplexray.an.ACTION_STOP_CORE"
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_START_CORE -> {
                Preferences(context).disableVpn = true
                val i = Intent(context, TProxyService::class.java).apply {
                    action = TProxyService.ACTION_START
                    putExtra(TProxyService.EXTRA_ORIGIN, TProxyService.ORIGIN_SFA)
                }
                if (Build.VERSION.SDK_INT >= 26) {
                    ContextCompat.startForegroundService(context, i)
                } else context.startService(i)
            }
            ACTION_STOP_CORE -> {
                val i = Intent(context, TProxyService::class.java).apply {
                    action = TProxyService.ACTION_DISCONNECT
                    putExtra(TProxyService.EXTRA_ORIGIN, TProxyService.ORIGIN_SFA)
                }
                if (Build.VERSION.SDK_INT >= 26) {
                    ContextCompat.startForegroundService(context, i)
                } else context.startService(i)
            }
        }
    }
}