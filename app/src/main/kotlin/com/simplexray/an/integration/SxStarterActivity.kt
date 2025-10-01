package com.simplexray.an.integration

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.simplexray.an.prefs.Preferences
import com.simplexray.an.service.TProxyService

class SxStarterActivity : Activity() {

    companion object {
        const val EXTRA_OP = "op"  // "start" | "stop"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Preferences(this).apply { disableVpn = true }

        val op = intent.getStringExtra(EXTRA_OP) ?: "start"
        val i = Intent(this, TProxyService::class.java).apply {
            action = if (op == "stop") TProxyService.ACTION_DISCONNECT
                     else TProxyService.ACTION_START
            putExtra(TProxyService.EXTRA_ORIGIN, TProxyService.ORIGIN_SFA)
        }
        if (Build.VERSION.SDK_INT >= 26) {
            ContextCompat.startForegroundService(this, i)
        } else {
            startService(i)
        }
        finish()
    }
}