# Description

Simplify notification operation, simple style, image style, inbox style, messaging style.

# Key Feature

- Simple Style Notification
- Image Style Notification
- Inbox Style Notification
- Messaging Style Notification
- Call Style Notification

# How To Use

## Base Notification Class

```kotlin
class AppPushlyNotification(context: Context) : BasePushlyNotification(context) {
    // override implementation
}
```

## Notification Channel

### Check Whether Notification Enabled

```kotlin
val pushlyNotification = AppPushlyNotification(applicationContext)
val isEnabled = pushlyNotification.isNotificationEnabled()
// perform result
```

### Create Channel

```kotlin
val pushlyNotification = AppPushlyNotification(applicationContext)
val notificationRingtoneUri =
    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
pushlyNotification.createNotificationChannel(
    channelId = "GENERAL-CHANNEL",
    channelName = "General",
    channelDescription = "General Notification",
    sound = notificationRingtoneUri,
    importance = NotificationManagerCompat.IMPORTANCE_DEFAULT
)
```

## Show Notification

### Show Simple Notification

```kotlin
val pushlyNotification = AppPushlyNotification(applicationContext)
val notification = pushlyNotification.getNotificationBuilder(
    channelId = "GENERAL-CHANNEL",
    title = "Simple Title Notification",
    message = "Simple Message Notification",
    smallIcon = R.drawable.il_media_islam,
    pendingIntent = null
).build()
pushlyNotification.showNotification(
    notificationId = 1,
    notification = notification
)
```

### Show Image Notification

```kotlin
val pushlyNotification = AppPushlyNotification(applicationContext)
Glide.with(this)
    .asBitmap()
    .load(Uri.parse("https://raw.githubusercontent.com/TutorialsBuzz/cdn/main/android.jpg"))
    .into(object : CustomTarget<Bitmap>() { 
        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) { 
            val imageNotification = pushlyNotification.getImageNotificationBuilder(
                channelId = "GENERAL-CHANNEL",
                title = "Simple Image Title Notification",
                message = "Simple Image Body Notification",
                bitmapImage = resource,
                pendingIntent = null,
                smallIcon = R.drawable.il_media_islam
            ).build()
            pushlyNotification.showNotification(2, imageNotification)
        }

        override fun onLoadCleared(placeholder: Drawable?) {} 
    })
```

### Show Inbox Notification

```kotlin
val pushlyNotification = AppPushlyNotification(applicationContext)
val inboxNotificatipn = pushlyNotification.getInboxStyleNotificationBuilder(
    channelId = "INBOX-CHANNEL",
    title = "3 Incoming Mail",
    message = "...",
    groupKey = "GROUP-KEY-1",
    summaryText = "Incoming Mail",
    lines = listOf("Hello", "How are u?", "I think ..."),
    pendingIntent = null,
    smallIcon = R.drawable.il_media_islam
).build()
pushlyNotification.showNotification(2, inboxNotificatipn)
```

### Show Message Notification

```kotlin
val pushlyNotification = AppPushlyNotification(applicationContext)
Glide.with(this)
    .asBitmap()
    .load("https://raw.githubusercontent.com/TutorialsBuzz/cdn/main/android.jpg")
    .into(object : CustomTarget<Bitmap>() {
        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            val notification = pushlyNotification.getMessagingStyleNotificationBuilder(
                title = "New Message",
                user = Person.Builder().setName("Taufik Fadlurahman Fajari")
                    .build(),
                channelId = "INBOX-CHANNEL",
                smallIcon = R.drawable.il_media_islam,
                message = "New Message From Bob",
                pendingIntent = null,
                summaryText = "Conversation",
                conversations = listOf(
                    ItemConversationNotificationModel(
                        message = "Hello Taufik!",
                        timestamp = System.currentTimeMillis(),
                        person = Person.Builder().setName("Bob")
                            .build()
                    ),
                    ItemConversationNotificationModel(
                        message = "Hi!",
                        timestamp = System.currentTimeMillis(),
                        person = Person.Builder().setName("Bob")
                            .apply {
                                setIcon(IconCompat.createWithBitmap(resource))
                            }
                            .build()
                    )
                )
            ).build()
            pushlyNotification.showNotification(10, notification)
        }

        override fun onLoadCleared(placeholder: Drawable?) {} 
    })
```

## Call Notification

To show call style notification, it needs a `Service` and `BroadcastReceiver` to handling audio & clickable action.

### Service

Service act as an initiator to trigger call notification in device.

In `onIncomingCall`, the app should create their own notification and start foreground using that notification.

```kotlin
class AppPushlyCallService : PushlyCallService() {
    override fun onIncomingCall(intent: Intent) {
        super.onIncomingCall(intent)
        // handle incoming call
    }
}
```

### Broadcast Receiver

Broadcast receiver is triggered when some action in call notification being clicked.

In `onAnswerCall` & `onDeclineCall` method, do not forget to stop media player.

In `onAnswerCall` method, you can handle to specific activity after user accept the call.

```kotlin
class AppPushlyCallReceiver : PushlyCallReceiver() {
    override fun onAnswerCall(context: Context, intent: Intent) {
        // handle answer call
    }

    override fun onDeclineCall(context: Context, intent: Intent) {
        // handle decline call
    }
}
```