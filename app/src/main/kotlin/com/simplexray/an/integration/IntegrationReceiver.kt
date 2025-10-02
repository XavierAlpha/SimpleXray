package com.simplexray.an.integration

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.simplexray.an.prefs.Preferences
import com.simplexray.an.service.TProxyService

class IntegrationReceiver : BroadcastReceiver() {

    companion object {
        const val ACT_FROM_SFA_START = "io.nekohasekai.sfa.CTRL_FROM_SFA_START"
        const val ACT_FROM_SFA_STOP  = "io.nekohasekai.sfa.CTRL_FROM_SFA_STOP"

        const val ACT_ACK_TO_SFA     = "com.simplexray.an.ACK_TO_SFA"

        const val EXTRA_REQUEST_ID   = "request_id"
        const val EXTRA_CALLER_PKG   = "caller_pkg"
        const val EXTRA_SHOW_UI      = "show_ui"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val op = when (intent.action) {
            ACT_FROM_SFA_START -> "start"
            ACT_FROM_SFA_STOP  -> "stop"
            else -> return
        }

        Preferences(context).apply { disableVpn = true }
        val svc = Intent(context, TProxyService::class.java).apply {
            action = if (op == "stop") TProxyService.ACTION_DISCONNECT
                     else TProxyService.ACTION_START
            putExtra(TProxyService.EXTRA_ORIGIN, TProxyService.ORIGIN_SFA)
        }
        ContextCompat.startForegroundService(context, svc)

        val reqId  = intent.getStringExtra(EXTRA_REQUEST_ID)
        val caller = intent.getStringExtra(EXTRA_CALLER_PKG)
        if (!reqId.isNullOrEmpty() && !caller.isNullOrEmpty()) {
            val ack = Intent(ACT_ACK_TO_SFA).apply {
                setPackage(caller)
                putExtra(EXTRA_REQUEST_ID, reqId)
            }
            runCatching { context.sendBroadcast(ack) }
        }
    }
}
