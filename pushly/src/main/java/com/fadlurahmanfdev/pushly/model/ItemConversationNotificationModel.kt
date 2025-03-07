package com.fadlurahmanfdev.pushly.model

import androidx.core.app.Person
import com.fadlurahmanfdev.pushly.common.BasePushlyNotification

/**
 * The class for wrap item of conversation.
 *
 * @param message the message of the conversation.
 * @param timestamp the time of the conversation.
 * @param person the user who send the message.
 *
 * @author fadlurahmanfdev - Taufik Fadlurahman Fajari
 *
 * @see [BasePushlyNotification.getMessagingStyleNotificationBuilder]
 * */
data class ItemConversationNotificationModel(
    val message: String,
    val timestamp: Long,
    val person: Person,
)
