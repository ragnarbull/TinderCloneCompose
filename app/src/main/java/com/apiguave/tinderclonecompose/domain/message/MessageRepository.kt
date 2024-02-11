package com.apiguave.tinderclonecompose.domain.message

import com.apiguave.tinderclonecompose.domain.match.entity.Match
import com.apiguave.tinderclonecompose.domain.message.entity.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    suspend fun getMatchById(matchId: String): Match?

    fun getMessages(matchId: String): Flow<List<Message>>

    suspend fun sendMessage(matchId: String, text: String)

    suspend fun likeMessage(matchId: String, messageId: String)

    suspend fun unLikeMessage(matchId: String, messageId: String)
}