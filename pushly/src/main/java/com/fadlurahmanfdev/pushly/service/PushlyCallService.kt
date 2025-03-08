package com.fadlurahmanfdev.pushly.service

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.fadlurahmanfdev.pushly.constant.PushlyConstant

abstract class PushlyCallService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(this::class.java.simpleName, "Pushly-LOG %%% - on received action ${intent?.action}")
        when (intent?.action) {
            PushlyConstant.ACTION_INCOMING_CALL -> {
                onIncomingCall(intent)
            }

            PushlyConstant.ACTION_STOP_INCOMING_CALL -> {
                onStopIncomingCall(intent)
            }
        }

        return START_STICKY
    }

    abstract fun onIncomingCall(intent: Intent)

    open fun onStopIncomingCall(intent: Intent) {
        stopService()
    }

    private fun stopService(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            stopForeground(true)
        }
    }
}