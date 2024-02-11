package com.apiguave.tinderclonecompose.domain.message.entity

data class Message(
    val id: String,
    val text: String,
    val isFromSender: Boolean,
    val liked: Boolean
)
