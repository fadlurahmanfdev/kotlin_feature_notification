package com.fadlurahmanfdev.pushly.common

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import com.fadlurahmanfdev.pushly.model.ItemConversationNotificationModel

abstract class BasePushlyNotification(val context: Context) {
    var notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


    fun getUriSoundFromResource(@RawRes soundNotification: Int): Uri {
        return Uri.parse("android.resource://" + context.packageName + "/" + soundNotification)
    }

    /**
     * Determine whether you have been granted a notification permission.
     * @author fadlurahmanfdev
     * @return return true if permission is enabled
     * @see BasePushlyNotification.createNotificationChannel
     * @see BasePushlyNotification.isSupportedNotificationChannel
     */
    fun isNotificationEnabled(): Boolean {
        return when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU -> {
                val isNotificationEnabled =
                    NotificationManagerCompat.from(context).areNotificationsEnabled()
                isNotificationEnabled
            }

            else -> {
                true
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
     * @param sound Sets the sound that should be played for notifications posted to this channel and its audio attributes
     * @param importance The importance of the channel. This controls how interruptive notifications posted to this channel are. (e.g., [NotificationManager.IMPORTANCE_LOW], [NotificationManager.IMPORTANCE_HIGH], [NotificationManager.IMPORTANCE_MAX])
     * */
    open fun createNotificationChannel(
        channelId: String,
        channelName: String,
        channelDescription: String,
        sound: Uri?,
        importance: Int,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                importance,
            ).apply {
                description = channelDescription
                setSound(sound, null)
            }
            createNotificationChannel(channel)
        }
    }

    /**
     * create notification channel
     * if notification channel is successfully created, it will return true, otherwise it will return false
     * @author fadlurahmanfdev
     * @return true if notification channel is created or device didn't support notification channel, and return false if notification channel is not successfully created
     * @param channel channel of the notification
     * */
    open fun createNotificationChannel(
        channel: NotificationChannel,
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Log.d(
                this::class.java.simpleName,
                "Pushly-LOG %%% - ${Build.VERSION.SDK_INT} is not need to create notification channel"
            )
            return
        }

        if (isNotificationChannelExist(channel.id)) {
            Log.d(
                this::class.java.simpleName,
                "Pushly-LOG %%% - notification channel with channelId: ${channel.id} already exist"
            )
            return
        }

        notificationManager.createNotificationChannel(channel)
        Log.i(
            this::class.java.simpleName,
            "Pushly-LOG %%% - successfully created notification channel ${channel.id} - ${channel.name}"
        )
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
                BasePushlyNotification::class.java.simpleName,
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
                BasePushlyNotification::class.java.simpleName,
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
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
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
            pendingIntent = pendingIntent,
        ).apply {
            setLargeIcon(bitmapImage)
            setStyle(
                NotificationCompat
                    .BigPictureStyle()
                    .bigPicture(bitmapImage)
                    .bigLargeIcon(bitmapImage)
            )
        }
    }

    fun getInboxStyleNotificationBuilder(
        channelId: String,
        title: String,
        text: String,
        groupKey: String,
        summaryText: String,
        lines: List<String>,
        @DrawableRes smallIcon: Int,
    ): NotificationCompat.Builder {
        val builder = NotificationCompat.Builder(context, channelId)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setSmallIcon(smallIcon)

        val inboxStyle = NotificationCompat.InboxStyle()
        if (lines.isNotEmpty()) {
            for (element in lines) {
                inboxStyle.addLine(element)
            }
            inboxStyle.setSummaryText(summaryText)
        }
        return builder.setStyle(inboxStyle)
            .setContentTitle(title)
            .setContentText(text)
            .setGroup(groupKey)
            .setGroupSummary(true)
    }

    open fun getMessagingStyleNotificationBuilder(
        notificationId: Int,
        channelId: String,
        @DrawableRes smallIcon: Int,
        title: String,
        user: Person,
        conversations: List<ItemConversationNotificationModel>
    ): NotificationCompat.Builder {
        val notificationBuilder = getNotificationBuilder(
            channelId = channelId,
            smallIcon = smallIcon,
            title = "TITLE",
            message = "MESSAGE",
            pendingIntent = null
        )
        val messagingStyle = NotificationCompat.MessagingStyle(user)
            .setConversationTitle(title)
        repeat(conversations.size) { index ->
            val conversation = conversations[index]
            messagingStyle.addMessage(
                NotificationCompat.MessagingStyle.Message(
                    conversation.message,
                    conversation.timestamp,
                    conversation.person,
                )
            )
            notificationBuilder.setStyle(messagingStyle)
        }
        return notificationBuilder
    }

    fun showNotification(notificationId: Int, notification: Notification) {
        notificationManager.notify(notificationId, notification)
    }

    fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }
}