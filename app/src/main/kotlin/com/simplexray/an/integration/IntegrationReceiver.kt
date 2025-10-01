package com.simplexray.an.integration

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.simplexray.an.prefs.Preferences
import com.simplexray.an.service.TProxyService

class IntegrationReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_START_CORE = "com.simplexray.an.ACTION_START_CORE"
        const val ACTION_STOP_CORE  = "com.simplexray.an.ACTION_STOP_CORE"
        const val ACTION_ACK        = "com.simplexray.an.ACTION_ACK"

        const val EXTRA_REQUEST_ID  = "request_id"
        const val EXTRA_CALLER_PKG  = "caller_pkg"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val prefs = Preferences(context)
        prefs.disableVpn = true

        val op = when (intent.action) {
            ACTION_STOP_CORE  -> "stop"
            ACTION_START_CORE -> "start"
            else -> return
        }

        val svc = Intent(context, TProxyService::class.java).apply {
            action = if (op == "stop") TProxyService.ACTION_DISCONNECT else TProxyService.ACTION_START
            putExtra(TProxyService.EXTRA_ORIGIN, TProxyService.ORIGIN_SFA)
        }
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            ContextCompat.startForegroundService(context, svc)
        } else {
            context.startService(svc)
        }

        val reqId = intent.getStringExtra(EXTRA_REQUEST_ID)
        val caller = intent.getStringExtra(EXTRA_CALLER_PKG)
        if (!reqId.isNullOrEmpty() && caller == "io.nekohasekai.sfa") {
            val ack = Intent(ACTION_ACK).apply {
                setPackage(caller)
                putExtra(EXTRA_REQUEST_ID, reqId)
            }
            runCatching {
                context.sendBroadcast(ack, "io.nekohasekai.sfa.permission.EXTERNAL_CONTROL")
            }
        }
    }
}
