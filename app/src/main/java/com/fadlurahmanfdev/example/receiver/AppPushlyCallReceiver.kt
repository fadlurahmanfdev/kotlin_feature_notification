package com.fadlurahmanfdev.example.receiver

import android.content.Context
import android.content.Intent
import com.fadlurahmanfdev.example.service.AppPushlyCallService
import com.fadlurahmanfdev.pushly.PushlyCallNotification
import com.fadlurahmanfdev.pushly.receiver.PushlyCallReceiver
import com.fadlurahmanfdev.pushly.service.PushlyCallService

class AppPushlyCallReceiver : PushlyCallReceiver() {
    override fun onAnswerCall(context: Context, intent: Intent) {
        PushlyCallNotification.stopIncomingCall(context, null, AppPushlyCallService::class.java)
    }
}