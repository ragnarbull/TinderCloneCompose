package com.apiguave.tinderclonecompose.data.datasource.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue

data class FirestoreMessage(
    val id: String="",
    val message: String="",
    val timestamp: Timestamp? = null,
    val senderId: String="",
    val liked: Boolean = false
)

object FirestoreMessageProperties{
    const val idKey = "id"
    const val messageKey = "message"
    const val timestampKey = "timestamp"
    const val senderIdKey = "senderId"
    const val likedKey = "liked"

    fun toData(id: String, userId: String, text: String, liked: Boolean): Map<String, Any> {
        return mapOf(
            idKey to id,
            messageKey to text,
            senderIdKey to userId,
            timestampKey to FieldValue.serverTimestamp(),
            likedKey to liked
        )
    }
}