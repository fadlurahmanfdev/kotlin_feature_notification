package com.fadlurahmanfdev.pushly.constant

import com.fadlurahmanfdev.pushly.exception.PushlyException

object PushlyConstantException {
    val SDK_DIDNT_SUPPORT_NOTIFICATION_CHANNEL =PushlyException(code = "SDK_DIDNT_SUPPORT_NOTIFICATION_CHANNEL", message = "The android SDK didn't support notification channel")
}