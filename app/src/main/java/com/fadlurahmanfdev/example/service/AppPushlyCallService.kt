package com.fadlurahmanfdev.example.service

import android.content.Intent
import androidx.core.app.Person
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
        val notification = pushlyNotification.getCallStyleNotificationBuilder(
            answerIntent = PushlyCallNotification.getAnswerPendingIntent(
                context = applicationContext,
                requestCode = 0,
                bundle = null,
                clazz = AppPushlyCallReceiver::class.java
            ),
            channelId = "CALL-CHANNEL",
            declineIntent = PushlyCallNotification.getAnswerPendingIntent(
                context = applicationContext,
                requestCode = 0,
                bundle = null,
                clazz = AppPushlyCallReceiver::class.java
            ),
            isVideo = false,
            message = "Message",
            pendingIntent = null,
            smallIcon = R.drawable.il_media_islam,
            title = "Title",
            user = Person.Builder().setName("Taufik Fadlurahman Fajari")
                .setImportant(true)
                .build()
        ).build()
        startForeground(1, notification)
    }
}