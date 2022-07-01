package com.mycompany.chatapp.notifications

data class Sender(
    val data: Data? = null,
    val to: String? = null,
    val content_available:Boolean,
    val priority: Int? = null
)

