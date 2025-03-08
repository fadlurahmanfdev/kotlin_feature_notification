package com.fadlurahmanfdev.pushly

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.fadlurahmanfdev.pushly.constant.PushlyConstant
import com.fadlurahmanfdev.pushly.presentation.BaseFullScreenIntentActivity
import com.fadlurahmanfdev.pushly.receiver.PushlyCallReceiver
import com.fadlurahmanfdev.pushly.service.PushlyCallService

class PushlyCallNotification {
    companion object {
        fun <T : PushlyCallService> showIncomingCall(
            context: Context,
            bundle: Bundle?,
            clazz: Class<T>
        ) {
            val intent = Intent(context, clazz)
            intent.apply {
                action = PushlyConstant.ACTION_INCOMING_CALL
                if (bundle != null) {
                    intent.putExtras(bundle)
                }
            }
            ContextCompat.startForegroundService(context, intent)
        }

        fun <T : PushlyCallService> stopIncomingCall(
            context: Context,
            bundle: Bundle?,
            clazz: Class<T>
        ) {
            val intent = Intent(context, clazz)
            intent.apply {
                action = PushlyConstant.ACTION_STOP_INCOMING_CALL
                if (bundle != null) {
                    intent.putExtras(bundle)
                }
            }
            ContextCompat.startForegroundService(context, intent)
        }

        fun <T : PushlyCallReceiver> getAnswerPendingIntent(
            context: Context,
            requestCode: Int,
            bundle: Bundle?,
            clazz: Class<T>
        ): PendingIntent {
            val intent = Intent(context, clazz).apply {
                action = PushlyConstant.ACTION_ANSWER_CALL
                if (bundle != null) {
                    putExtras(bundle)
                }
            }
            return PendingIntent.getBroadcast(context, requestCode, intent, flagPendingIntent())
        }

        fun <T : PushlyCallReceiver> getDeclinePendingIntent(
            context: Context,
            requestCode: Int,
            bundle: Bundle?,
            clazz: Class<T>
        ): PendingIntent {
            val intent = Intent(context, clazz).apply {
                action = PushlyConstant.ACTION_DECLINE_CALL
                if (bundle != null) {
                    putExtras(bundle)
                }
            }
            return PendingIntent.getBroadcast(context, requestCode, intent, flagPendingIntent())
        }

        fun <T : BaseFullScreenIntentActivity> getFullScreenPendingIntent(
            context: Context,
            requestCode: Int,
            bundle: Bundle?,
            clazz: Class<T>
        ): PendingIntent {
            val intent = Intent(context, clazz).apply {
                if (bundle != null) {
                    putExtras(bundle)
                }
            }
            return PendingIntent.getActivity(context, requestCode, intent, flagPendingIntent())
        }

        private fun flagPendingIntent(): Int {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        }
    }
}