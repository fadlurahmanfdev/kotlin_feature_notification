package com.fadlurahmanfdev.pushly.model

import androidx.core.app.Person

data class ItemConversationNotificationModel(
    val message: String,
    val timestamp: Long,
    val person: Person,
)
