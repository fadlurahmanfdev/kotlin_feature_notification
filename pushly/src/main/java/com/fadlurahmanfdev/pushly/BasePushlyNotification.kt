package com.fadlurahmanfdev.pushly

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import com.fadlurahmanfdev.pushly.constant.PushlyConstantException
import com.fadlurahmanfdev.pushly.model.ItemConversationNotificationModel

abstract class BasePushlyNotification(val context: Context) {
    var notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    /**
     * Get uri sound from drawable resource.
     * @param soundNotification sound resource
     * @author fadlurahmanfdev - Taufik Fadlurahman Fajari
     * */
    fun getUriSoundFromResource(@RawRes soundNotification: Int): Uri {
        return Uri.parse("android.resource://" + context.packageName + "/" + soundNotification)
    }

    /**
     * Determine whether you have been granted a notification permission.
     *
     * this method only available if android SDK < [Build.VERSION_CODES.TIRAMISU], otherwise it will always return true
     *
     * @author fadlurahmanfdev
     * @return true if permission is enabled
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

    open fun isSupportedNotificationChannel(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }

    /**
     * Create a notification channel.
     *
     * Notification channel will exist in app settings inside device settings.
     *
     * @author fadlurahmanfdev - Taufik Fadlurahman Fajari
     * @param channelId unique identifier of channel.
     * @param channelDescription channel description will be shown to user in notification.
     * @param channelName the description of the channel.
     * @param importance the importance level of this notification channel. This controls how interruptive notifications posted to this channel are. (e.g., [NotificationManagerCompat.IMPORTANCE_LOW], [NotificationManagerCompat.IMPORTANCE_HIGH], [NotificationManagerCompat.IMPORTANCE_MAX]).
     * @param sound sound for this notification channel when pop-up, see [getUriSoundFromResource].
     * */
    open fun createNotificationChannel(
        channelId: String,
        channelDescription: String,
        channelName: String,
        importance: Int,
        sound: Uri?,
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
     * Create a notification channel.
     *
     * Notification channel will exist in app settings inside device settings.
     *
     * @author fadlurahmanfdev - Taufik Fadlurahman Fajari
     * @param channel the channel to create.
     * @see createNotificationChannel
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
     * Check whether the notification channel exists.
     *
     * @param channelId unique identifier of the notification channel.
     * @return true if notification channel exists.
     * @author fadlurahmanfdev - Taufik Fadlurahman Fajari
     * @exception [PushlyConstantException.SDK_DIDNT_SUPPORT_NOTIFICATION_CHANNEL] if android sdk is not supported
     * @see createNotificationChannel
     * @see createNotificationChannel
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
        }

        throw PushlyConstantException.SDK_DIDNT_SUPPORT_NOTIFICATION_CHANNEL
    }


    /**
     * Delete notification channel.
     *
     * @author fadlurahmanfdev - Taufik Fadlurahman Fajari
     * @return true if notification channel is successfully deleted
     * @exception [PushlyConstantException.SDK_DIDNT_SUPPORT_NOTIFICATION_CHANNEL] if android sdk is not supported
     * */
    open fun deleteNotificationChannel(channelId: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (isNotificationChannelExist(channelId)) {
                notificationManager.deleteNotificationChannel(channelId)
                return !isNotificationChannelExist(channelId)
            }
            return true
        }

        throw PushlyConstantException.SDK_DIDNT_SUPPORT_NOTIFICATION_CHANNEL
    }

    /**
     * Get based notification before show using [showNotification].
     *
     * Channel id must be created first, it the channel id not exists yet, the notification will not show/pop up
     *
     * @param channelId unique identifier of the channel id.
     * @param message the message will shown to user.
     * @param pendingIntent the intent to handle if notification being clicked.
     * @param priority the level priority of this notification.
     * @param smallIcon the notification icon.
     * @param title the title will shown to user.
     *
     * @author fadlurahmanfdev - Taufik Fadlurahman Fajari
     * */
    open fun getNotificationBuilder(
        channelId: String,
        message: String,
        pendingIntent: PendingIntent?,
        priority: Int = NotificationCompat.PRIORITY_DEFAULT,
        @DrawableRes smallIcon: Int,
        title: String,
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, channelId)
            .setPriority(priority)
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

    /**
     * Get based image notification before show using [showNotification].
     *
     * Channel id must be created first, it the channel id not exists yet, the notification will not show/pop up
     *
     * @param bitmapImage image in bitmap for shown into user.
     * @param channelId unique identifier of the channel id.
     * @param message the message will shown to user.
     * @param pendingIntent the intent to handle if notification being clicked.
     * @param priority the level priority of this notification.
     * @param smallIcon the notification icon.
     * @param title the title will shown to user.
     *
     * @author fadlurahmanfdev - Taufik Fadlurahman Fajari
     * */
    open fun getImageNotificationBuilder(
        bitmapImage: Bitmap,
        channelId: String,
        message: String,
        pendingIntent: PendingIntent?,
        priority: Int = NotificationCompat.PRIORITY_DEFAULT,
        @DrawableRes smallIcon: Int,
        title: String,
    ): NotificationCompat.Builder {
        return getNotificationBuilder(
            channelId = channelId,
            message = message,
            pendingIntent = pendingIntent,
            priority = priority,
            smallIcon = smallIcon,
            title = title,
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

    /**
     * Get based inbox notification before show using [showNotification].
     *
     * Channel id must be created first, it the channel id not exists yet, the notification will not show/pop up
     *
     * @param channelId unique identifier of the channel id.
     * @param groupKey the group key of the notification.
     * @param lines list of content notification.
     * @param message the message will shown to user.
     * @param pendingIntent the intent to handle if notification being clicked.
     * @param priority the level priority of this notification.
     * @param smallIcon the notification icon.
     * @param summaryText the summary text of the notification.
     * @param title the title will shown to user.
     *
     * @author fadlurahmanfdev - Taufik Fadlurahman Fajari
     * */
    fun getInboxStyleNotificationBuilder(
        channelId: String,
        groupKey: String,
        lines: List<String>,
        message: String,
        pendingIntent: PendingIntent?,
        priority: Int = NotificationCompat.PRIORITY_DEFAULT,
        @DrawableRes smallIcon: Int,
        summaryText: String,
        title: String,
    ): NotificationCompat.Builder {
        val builder = getNotificationBuilder(
            channelId = channelId,
            message = message,
            pendingIntent = pendingIntent,
            priority = priority,
            smallIcon = smallIcon,
            title = title,
        )

        val inboxStyle = NotificationCompat.InboxStyle()
        if (lines.isNotEmpty()) {
            for (element in lines) {
                inboxStyle.addLine(element)
            }
        }
        inboxStyle.setSummaryText(summaryText)
        return builder.setStyle(inboxStyle)
            .setGroup(groupKey)
            .setGroupSummary(true)
    }

    /**
     * Get based messaging notification before show using [showNotification].
     *
     * Channel id must be created first, it the channel id not exists yet, the notification will not show/pop up
     *
     * @param channelId unique identifier of the channel id.
     * @param conversations list conversation for display to user
     * @param message the message will shown to user.
     * @param pendingIntent the intent to handle if notification being clicked.
     * @param priority the level priority of this notification.
     * @param smallIcon the notification icon.
     * @param summaryText the summary text of the notification.
     * @param title the title will shown to user.
     * @param user the user of this device
     *
     * @author fadlurahmanfdev - Taufik Fadlurahman Fajari
     * */
    open fun getMessagingStyleNotificationBuilder(
        channelId: String,
        conversations: List<ItemConversationNotificationModel>,
        message: String,
        pendingIntent: PendingIntent?,
        priority: Int = NotificationCompat.PRIORITY_DEFAULT,
        @DrawableRes smallIcon: Int,
        summaryText: String,
        title: String,
        user: Person,
    ): NotificationCompat.Builder {
        val builder = getNotificationBuilder(
            channelId = channelId,
            message = message,
            pendingIntent = pendingIntent,
            priority = priority,
            smallIcon = smallIcon,
            title = title,
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
        }
        builder.setStyle(messagingStyle)
        return builder
    }

    open fun getCallStyleNotificationBuilder(
        answerIntent: PendingIntent,
        channelId: String,
        declineIntent: PendingIntent,
        isVideo:Boolean,
        message: String,
        pendingIntent: PendingIntent?,
        priority: Int = NotificationCompat.PRIORITY_MAX,
        @DrawableRes smallIcon: Int,
        title: String,
        user: Person,
    ): NotificationCompat.Builder {
        val builder = getNotificationBuilder(
            channelId = channelId,
            message = message,
            pendingIntent = pendingIntent,
            priority = priority,
            smallIcon = smallIcon,
            title = title,
        )

        val callStyle = NotificationCompat.CallStyle.forScreeningCall(user, answerIntent, declineIntent)
            .setIsVideo(isVideo)
        builder.setStyle(callStyle)
        return builder
    }

    /**
     * Show notification to user.
     *
     * Channel id must be created first, it the channel id not exists yet, the notification will not show/pop up
     *
     * @param notificationId the identifier of this notification.
     * @param notification the notification to shown to user.
     *
     * @author fadlurahmanfdev - Taufik Fadlurahman Fajari
     *
     * @see createNotificationChannel
     * */
    fun showNotification(notificationId: Int, notification: Notification) {
        if (isSupportedNotificationChannel()) {
            if (!isNotificationChannelExist(notification.channelId)) {
                Log.w(
                    this::class.java.simpleName,
                    "Pushly-LOG %%% - notification channel with channel id ${notification.channelId} is not exist, the notification will not shown"
                )
            }
        }
        notificationManager.notify(notificationId, notification)
    }

    /**
     * Cancel notification.
     *
     * @param notificationId the identifier of this notification.
     *
     * @author fadlurahmanfdev - Taufik Fadlurahman Fajari
     *
     * @see showNotification
     * */
    fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }
}