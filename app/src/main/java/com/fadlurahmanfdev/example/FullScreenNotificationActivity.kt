package com.fadlurahmanfdev.example

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fadlurahmanfdev.pushly.presentation.BaseFullScreenIntentActivity

class FullScreenNotificationActivity : BaseFullScreenIntentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_full_screen_notification)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onCreateBaseFullScreenIntentActivity(savedInstanceState: Bundle?) {

    }

    override fun onDestroyBaseFullScreenIntentActivity() {

    }
}