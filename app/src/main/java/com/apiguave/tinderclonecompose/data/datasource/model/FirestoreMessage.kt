package com.apiguave.tinderclonecompose.data.datasource.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue

data class FirestoreMessage(
    val id: String = "",
    val timestamp: Timestamp? = null,
    val senderId: String = "",
    val liked: Boolean = false,
    val message: String? = null,
    val giphyMediaId: String? = null
)

object FirestoreMessageProperties{
    const val idKey = "id"
    const val timestampKey = "timestamp"
    const val senderIdKey = "senderId"
    const val likedKey = "liked"
    const val messageKey = "message"
    const val giphyMediaIdKey = "giphyMediaId"

    fun toData(
        id: String,
        userId: String,
        liked: Boolean,
        text: String?,
        giphyMediaId: String?
    ): Map<String, Any?> {
        return mapOf(
            idKey to id,
            senderIdKey to userId,
            timestampKey to FieldValue.serverTimestamp(),
            likedKey to liked,
            messageKey to text,
            giphyMediaIdKey to giphyMediaId
        )
    }
}