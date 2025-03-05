package com.fadlurahmanfdev.pushly.common

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.fadlurahmanfdev.pushly.enums.FeatureNotificationImportance

abstract class BaseFeatureNotification(val context: Context) {
    var notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


    fun getUriSoundFromResource(@RawRes soundNotification: Int): Uri {
        return Uri.parse("android.resource://" + context.packageName + "/" + soundNotification)
    }

    /**
     * Determine whether you have been granted a notification permission.
     * @author fadlurahmanfdev
     * @return return true if permission is [PackageManager.PERMISSION_GRANTED] and return false if
     * permission is [PackageManager.PERMISSION_DENIED].
     * @see BaseFeatureNotification.createNotificationChannel
     * @see BaseFeatureNotification.isSupportedNotificationChannel
     */
    open fun isNotificationPermissionEnabledAndGranted(): Boolean {
        return when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU -> {
                val isNotificationEnabled =
                    NotificationManagerCompat.from(context).areNotificationsEnabled()
                isNotificationEnabled
            }

            else -> {
                val status = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission_group.NOTIFICATIONS
                )
                status == PackageManager.PERMISSION_GRANTED
            }
        }
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
    open fun isSupportedNotificationChannel(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }

    /**
     * create notification channel
     * if notification channel is successfully created, it will return true, otherwise it will return false
     * @author fadlurahmanfdev
     * @return true if notification channel is created or device didn't support notification channel, and return false if notification channel is not successfully created
     * @param channelId unique identifier for different channelId created for the apps
     * @param channelName channel name will be shown to user in notification
     * @param channelDescription channel description will be shown to user in notification
     * */
    open fun createNotificationChannel(
        channelId: String,
        channelName: String,
        channelDescription: String,
        sound: Uri?,
        importance: FeatureNotificationImportance = FeatureNotificationImportance.IMPORTANCE_DEFAULT,
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            Log.i(this::class.java.simpleName, "${Build.VERSION.SDK_INT} is not need to create notification channel")
            return
        }

        if (isNotificationChannelExist(channelId)) {
            Log.i(this::class.java.simpleName, "Notification channel with channelId: $channelId already exist")
            return
        }

        val channel = NotificationChannel(
            channelId,
            channelName,
            importance.value,
        ).apply {
            description = channelDescription
            setSound(sound, null)
        }
        notificationManager.createNotificationChannel(channel)
    }

    /**
     * check whether is notification channel is exist
     * return true if notification channel is exist
     * */
    open fun isNotificationChannelExist(channelId: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val allChannels = notificationManager.notificationChannels
            var knownChannel: NotificationChannel? = null
            for (element in allChannels) {
                if (element.id == channelId) {
                    knownChannel = element
                    break
                }
            }
            return knownChannel != null
        } else {
            Log.i(
                BaseFeatureNotification::class.java.simpleName,
                "${Build.VERSION.SDK_INT} is not supported to get notification channel"
            )
            return true
        }
    }


    /**
     * delete notification channel
     * return true if notification channel is successfully deleted
     * */
    open fun deleteNotificationChannel(channelId: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (isNotificationChannelExist(channelId)) {
                notificationManager.deleteNotificationChannel(channelId)
                return !isNotificationChannelExist(channelId)
            }
            return true
        } else {
            Log.i(
                BaseFeatureNotification::class.java.simpleName,
                "${Build.VERSION.SDK_INT} is not supported to delete notification channel"
            )
            return true
        }
    }

    open fun getNotificationBuilder(
        channelId: String,
        @DrawableRes smallIcon: Int,
        title: String,
        message: String,
        pendingIntent: PendingIntent?,
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, channelId)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setSmallIcon(smallIcon)
            .setContentTitle(title)
            .setContentText(message)
            .apply {
                if (pendingIntent != null) {
                    setContentIntent(pendingIntent)
                }
            }
    }

    fun groupNotificationBuilder(
        context: Context,
        channelId: String,
        groupKey: String,
        bigContentTitle: String,
        summaryText: String,
        lines: List<String>,
        @DrawableRes smallIcon: Int,
    ): NotificationCompat.Builder {
        val builder = NotificationCompat.Builder(context, channelId)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setSmallIcon(smallIcon)

        val inboxStyle = NotificationCompat.InboxStyle()
        if (lines.isNotEmpty()) {
            for (element in lines) {
                inboxStyle.addLine(element)
            }
            inboxStyle.setBigContentTitle(bigContentTitle)
                .setSummaryText(summaryText)
        }
        return builder.setStyle(inboxStyle)
            .setGroup(groupKey)
            .setGroupSummary(true)
    }

    open fun getImageNotificationBuilder(
        channelId: String,
        @DrawableRes smallIcon: Int,
        title: String,
        message: String,
        pendingIntent: PendingIntent?,
        bitmapImage: Bitmap,
    ): NotificationCompat.Builder {
        return getNotificationBuilder(
            channelId = channelId,
            smallIcon = smallIcon,
            title = title,
            message = message,
            pendingIntent = pendingIntent
        ).apply {
            setLargeIcon(bitmapImage)
            setStyle(NotificationCompat
                .BigPictureStyle()
                .bigPicture(bitmapImage)
                .bigLargeIcon(bitmapImage)
            )
        }
    }

    fun showNotification(notificationId: Int, notification: Notification) {
        notificationManager.notify(notificationId, notification)
    }

    fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }
}