package com.mycompany.chatapp.model

data class  Chat(
    val sender: String? = null,
    val receiver: String? = null,
    val message: String? = null,
    val isseen: Boolean? = false
)
