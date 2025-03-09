package com.fadlurahmanfdev.pushly.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.fadlurahmanfdev.pushly.PushlyCallNotification
import com.fadlurahmanfdev.pushly.constant.PushlyConstant
import com.fadlurahmanfdev.pushly.service.PushlyCallService

abstract class PushlyCallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(this::class.java.simpleName, "Pushly-LOG %%% on receive action ${intent?.action}")
        if (context == null) return
        when (intent?.action) {
            PushlyConstant.ACTION_ANSWER_CALL -> {
                onAnswerCall(context, intent)
            }

            PushlyConstant.ACTION_DECLINE_CALL -> {
                onDeclineCall(context, intent)
            }
        }
    }

    abstract fun onAnswerCall(context: Context, intent: Intent)

    abstract fun onDeclineCall(context: Context, intent: Intent)
}