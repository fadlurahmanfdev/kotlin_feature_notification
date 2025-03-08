package com.fadlurahmanfdev.example.service

import android.content.Intent
import androidx.core.app.Person
import com.fadlurahmanfdev.example.FullScreenNotificationActivity
import com.fadlurahmanfdev.example.R
import com.fadlurahmanfdev.example.domain.AppPushlyNotification
import com.fadlurahmanfdev.example.receiver.AppPushlyCallReceiver
import com.fadlurahmanfdev.pushly.PushlyCallNotification
import com.fadlurahmanfdev.pushly.service.PushlyCallService

class AppPushlyCallService : PushlyCallService() {
    private lateinit var pushlyNotification:AppPushlyNotification

    override fun onCreate() {
        super.onCreate()
        pushlyNotification = AppPushlyNotification(applicationContext)
    }

    override fun onIncomingCall(intent: Intent) {
        super.onIncomingCall(intent)
        val notification = pushlyNotification.getCallStyleNotificationBuilder(
            answerIntent = PushlyCallNotification.getAnswerPendingIntent(
                context = applicationContext,
                requestCode = 0,
                bundle = null,
                clazz = AppPushlyCallReceiver::class.java
            ),
            answerText = "Answer",
            channelId = "CALL-CHANNEL",
            declineIntent = PushlyCallNotification.getDeclinePendingIntent(
                context = applicationContext,
                requestCode = 1,
                bundle = null,
                clazz = AppPushlyCallReceiver::class.java
            ),
            fullScreenIntent = PushlyCallNotification.getFullScreenPendingIntent(
                context = applicationContext,
                requestCode = 2,
                bundle = null,
                clazz = FullScreenNotificationActivity::class.java
            ),
            declineText = "Decline",
            isVideo = false,
            message = "Incoming Call",
            pendingIntent = null,
            smallIcon = R.drawable.il_media_islam,
            user = Person.Builder().setName("Taufik Fadlurahman Fajari")
                .setImportant(true)
                .build()
        ).build()
        startForeground(1, notification)
    }
}