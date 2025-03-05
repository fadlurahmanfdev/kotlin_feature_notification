package com.github.fadlurahmanfdev.core_notification_example

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.fadlurahmanfdev.core_notification_example.adapter.ListExampleAdapter
import com.github.fadlurahmanfdev.core_notification_example.model.FeatureModel
import com.github.fadlurahmanfdev.core_notification_example.repository.AppMessagingFeatureNotificationImpl
import com.github.fadlurahmanfdev.core_notification_example.repository.AppFeatureNotificationRepositoryImpl
import com.github.fadlurahmanfdev.core_notification_example.usecase.ExampleNotificationUseCaseImpl
import com.github.fadlurahmanfdev.core_notification_example.view_model.ExampleNotificationViewModel

class MainActivity : AppCompatActivity(), ListExampleAdapter.Callback {
    lateinit var rv: RecyclerView
    lateinit var viewModel: ExampleNotificationViewModel

    private val features: List<FeatureModel> = listOf<FeatureModel>(
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Ask Permission",
            desc = "Ask Permission Notification",
            enum = "ASK_PERMISSION"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Create General Channel Notification",
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

        viewModel = ExampleNotificationViewModel(
            exampleNotificationUseCase = ExampleNotificationUseCaseImpl(
                appFeatureNotificationRepository = AppFeatureNotificationRepositoryImpl(
                    this
                ),
                appMessagingNotificationRepository = AppMessagingFeatureNotificationImpl(
                    this
                )
            ),
            appFeatureNotificationRepository = AppFeatureNotificationRepositoryImpl(
                context = this
            ),
        )

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
                viewModel.askPermission(this)
            }

            "CREATE_GENERAL_CHANNEL_NOTIFICATION"-> {
                viewModel.createGeneralChannelNotification()
            }

            "SHOW_NOTIFICATION" -> {
                viewModel.showNotification()
            }

            "SHOW_IMAGE_NOTIFICATION" -> {
                viewModel.showImageNotification(this)
            }

            "SHOW_INBOX_NOTIFICATION" -> {
                viewModel.showInboxNotification(this)
            }

            "SHOW_CONVERSATION_NOTIFICATION" -> {
                viewModel.showConversationNotification(this)
            }

            "SHOW_FULL_SCREEN_NOTIFICATION" -> {
                val intent = Intent(this, FullScreenNotificationActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(this, 1, intent, flagPendingIntent())
                viewModel.showFullScreenNotification(this, pendingIntent)
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