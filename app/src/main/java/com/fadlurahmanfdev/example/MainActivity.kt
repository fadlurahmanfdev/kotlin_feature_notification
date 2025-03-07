package com.fadlurahmanfdev.example

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.fadlurahmanfdev.example.adapter.ListExampleAdapter
import com.fadlurahmanfdev.example.domain.AppPushlyNotification
import com.fadlurahmanfdev.example.model.FeatureModel
import com.fadlurahmanfdev.example.model.ItemPerson
import com.fadlurahmanfdev.pushly.model.ItemConversationNotificationModel

class MainActivity : AppCompatActivity(), ListExampleAdapter.Callback {
    lateinit var rv: RecyclerView
    lateinit var pushlyNotification: AppPushlyNotification

    private val features: List<FeatureModel> = listOf<FeatureModel>(
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Is Notification Enabled",
            desc = "Check whether notification enabled",
            enum = "IS_NOTIFICATION_ENABLED"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "--DIVIDER SIMPLE NOTIFICATION---",
            desc = "------------------------------------------------------------",
            enum = "--DIVIDER SIMPLE NOTIFICATION---"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "General Channel",
            desc = "Create General Channel Notification",
            enum = "CREATE_GENERAL_CHANNEL_NOTIFICATION"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Show Notification",
            desc = "Show notification",
            enum = "SHOW_NOTIFICATION"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Show Image Notification",
            desc = "Show notification with image",
            enum = "SHOW_IMAGE_NOTIFICATION"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "--DIVIDER INBOX NOTIFICATION---",
            desc = "------------------------------------------------------------",
            enum = "--DIVIDER INBOX NOTIFICATION---"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Inbox Channel",
            desc = "Create Inbox Channel Notification",
            enum = "CREATE_INBOX_CHANNEL_NOTIFICATION"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Show Inbox Notification",
            desc = "Show notification with list of inbox",
            enum = "SHOW_INBOX_NOTIFICATION"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Show Conversation Notification",
            desc = "Show notification with conversation style",
            enum = "SHOW_CONVERSATION_NOTIFICATION"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "--DIVIDER ALARM NOTIFICATION---",
            desc = "------------------------------------------------------------",
            enum = "--DIVIDER ALARM NOTIFICATION---"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Inbox Channel",
            desc = "Create Inbox Channel Notification",
            enum = "CREATE_ALARM_CHANNEL_NOTIFICATION"
        ),FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Show Full Screen Notification",
            desc = "Show Full Screen notification",
            enum = "SHOW_FULL_SCREEN_NOTIFICATION"
        ),
    )

    private lateinit var adapter: ListExampleAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        rv = findViewById(R.id.rv)
        rv.setItemViewCacheSize(features.size)
        rv.setHasFixedSize(true)

        pushlyNotification = AppPushlyNotification(this)

        adapter = ListExampleAdapter()
        adapter.setCallback(this)
        adapter.setList(features)
        adapter.setHasStableIds(true)
        rv.adapter = adapter
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            Log.d(MainActivity::class.java.simpleName, "IS GRANTED -> $isGranted")
        }

    override fun onClicked(item: FeatureModel) {
        when (item.enum) {
            "ASK_PERMISSION" -> {
                val isEnabled = pushlyNotification.isNotificationEnabled()
                Log.d(
                    this::class.java.simpleName,
                    "Pushly-LOG %%% - is notification enabled: $isEnabled"
                )
            }

            "CREATE_GENERAL_CHANNEL_NOTIFICATION" -> {
                val notificationRingtoneUri =
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                pushlyNotification.createNotificationChannel(
                    channelId = "GENERAL",
                    channelName = "Umum",
                    channelDescription = "Notifikasi Umum",
                    sound = notificationRingtoneUri,
                    importance = NotificationManagerCompat.IMPORTANCE_DEFAULT
                )
            }

            "SHOW_NOTIFICATION" -> {
                val notification = pushlyNotification.getNotificationBuilder(
                    channelId = "GENERAL",
                    title = "Simple Title Notification",
                    message = "Simple Message Notification",
                    smallIcon = R.drawable.il_media_islam,
                    pendingIntent = null
                ).build()
                pushlyNotification.showNotification(
                    notificationId = 1,
                    notification = notification
                )
            }

            "SHOW_IMAGE_NOTIFICATION" -> {
                Glide.with(this)
                    .asBitmap()
                    .load(Uri.parse("https://raw.githubusercontent.com/TutorialsBuzz/cdn/main/android.jpg"))
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            val imageNotification = pushlyNotification.getImageNotificationBuilder(
                                channelId = "GENERAL",
                                title = "Simple Image Title Notification",
                                message = "Simple Image Body Notification",
                                bitmapImage = resource,
                                pendingIntent = null,
                                smallIcon = R.drawable.il_media_islam
                            ).build()
                            pushlyNotification.showNotification(2, imageNotification)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {

                        }

                    })
            }

            "CREATE_INBOX_CHANNEL_NOTIFICATION" -> {
                pushlyNotification.createNotificationChannel(
                    channelId = "INBOX-CHANNEL",
                    channelName = "Inbox",
                    channelDescription = "Notifikasi Inbox",
                    sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    importance = NotificationManagerCompat.IMPORTANCE_DEFAULT
                )
            }

            "SHOW_INBOX_NOTIFICATION" -> {
                val inboxNotificatipn = pushlyNotification.getInboxStyleNotificationBuilder(
                    channelId = "INBOX-CHANNEL",
                    title = "New Email Incoming",
                    message = "3 Mails from fadlurahmanfdev",
                    groupKey = "GROUP-KEY-1",
                    summaryText = "Incoming Mail",
                    lines = listOf("Hello", "How are u?", "I think ..."),
                    pendingIntent = null,
                    smallIcon = R.drawable.il_media_islam
                ).build()
                pushlyNotification.showNotification(2, inboxNotificatipn)
            }

            "SHOW_CONVERSATION_NOTIFICATION" -> {
                val mapUser = hashMapOf<String, Bitmap?>()
                val persons = listOf(
                    ItemPerson(
                        id = "ALICE",
                        name = "Alice",
                        image = "https://plus.unsplash.com/premium_photo-1669740216382-71b87585ab1e?q=80&w=3087&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
                    ),
                    ItemPerson(
                        id = "BOB",
                        name = "Bob",
                        image = "https://images.unsplash.com/photo-1551847812-f815b31ae67c?q=80&w=3164&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
                    )
                )
                repeat(persons.size) { personIndex ->
                    val person = persons[personIndex]
                    if (person.image != null) {
                        Glide.with(this)
                            .asBitmap()
                            .load(Uri.parse(person.image))
                            .into(object : CustomTarget<Bitmap>() {
                                override fun onResourceReady(
                                    resource: Bitmap,
                                    transition: Transition<in Bitmap>?
                                ) {
                                    mapUser[person.id] = resource
                                    if (mapUser.keys.size == persons.size) {
                                        val notification = pushlyNotification.getMessagingStyleNotificationBuilder(
                                            title = "Conversation Title",
                                            user = Person.Builder().setName("fadlurahmanfdev")
                                                .build(),
                                            channelId = "INBOX-CHANNEL",
                                            smallIcon = R.drawable.il_media_islam,
                                            message = "Conversation Message",
                                            pendingIntent = null,
                                            summaryText = "summart conversation",
                                            conversations = listOf(
                                                ItemConversationNotificationModel(
                                                    message = "Hello Alice!",
                                                    timestamp = System.currentTimeMillis(),
                                                    person = Person.Builder().setName("Alice")
                                                        .build()
                                                ),
                                                ItemConversationNotificationModel(
                                                    message = "Hi!",
                                                    timestamp = System.currentTimeMillis(),
                                                    person = Person.Builder().setName("Bob")
                                                        .apply {
                                                            if (mapUser["BOB"] != null) {
                                                                setIcon(
                                                                    IconCompat.createWithBitmap(
                                                                        mapUser["BOB"]!!
                                                                    )
                                                                )
                                                            }
                                                        }
                                                        .build()
                                                )
                                            )
                                        ).build()
                                        pushlyNotification.showNotification(10, notification)
                                    }
                                }

                                override fun onLoadCleared(placeholder: Drawable?) {
                                    mapUser[person.id] = null
                                }
                            })
                    } else {
                        mapUser[person.id] = null
                    }
                }
            }

            "CREATE_ALARM_CHANNEL_NOTIFICATION" -> {
                pushlyNotification.createNotificationChannel(
                    channelId = "ALARM-CHANNEL",
                    channelName = "Inbox",
                    channelDescription = "Notifikasi Inbox",
                    sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM),
                    importance = NotificationManagerCompat.IMPORTANCE_MAX
                )
            }

            "SHOW_FULL_SCREEN_NOTIFICATION" -> {
                val intent = Intent(this, FullScreenNotificationActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(this, 1, intent, flagPendingIntent())
                val notification = pushlyNotification.getNotificationBuilder(
                    channelId = "ALARM-CHANNEL",
                    title = "Alarm Title",
                    message = "Alarm Message",
                    smallIcon = R.drawable.il_media_islam,
                    pendingIntent = pendingIntent
                ).apply {
                    setContentText("FULL SCREEN TEXT")
                    setContentTitle("FULL SCREEN TITLE")
                    setFullScreenIntent(
                        pendingIntent,
                        true
                    )
                    priority = NotificationCompat.PRIORITY_MAX
                }.build()
                pushlyNotification.showNotification(
                    notificationId = 1,
                    notification = notification
                )
            }

            "" -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissionLauncher.launch(android.Manifest.permission_group.NOTIFICATIONS)
                } else {

                }

            }
        }
    }

    private fun flagPendingIntent(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
    }
}