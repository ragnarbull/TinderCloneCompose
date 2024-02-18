package com.apiguave.tinderclonecompose.domain.message.entity

data class Message(
    val id: String,
    val isFromSender: Boolean,
    val liked: Boolean,
    val text: String?,
    val giphyMediaId: String?
)
