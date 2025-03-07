package com.fadlurahmanfdev.pushly.exception

data class PushlyException(
    val code: String,
    override val message: String?,
) : Throwable(message = message)
