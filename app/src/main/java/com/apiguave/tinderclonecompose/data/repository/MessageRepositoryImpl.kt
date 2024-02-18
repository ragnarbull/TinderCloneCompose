package com.apiguave.tinderclonecompose.data.repository

import com.apiguave.tinderclonecompose.data.datasource.AuthRemoteDataSource
import com.apiguave.tinderclonecompose.data.datasource.FirestoreRemoteDataSource
import com.apiguave.tinderclonecompose.data.datasource.StorageRemoteDataSource
import com.apiguave.tinderclonecompose.data.datasource.model.FirestoreMatch
import com.apiguave.tinderclonecompose.domain.match.entity.Match
import com.apiguave.tinderclonecompose.domain.message.MessageRepository
import com.apiguave.tinderclonecompose.extensions.toAge
import com.apiguave.tinderclonecompose.extensions.toShortString

class MessageRepositoryImpl(
    private val authDataSource: AuthRemoteDataSource,
    private val storageDataSource: StorageRemoteDataSource,
    private val firestoreDataSource : FirestoreRemoteDataSource): MessageRepository {

    override suspend fun getMatchById(matchId: String): Match? {
        return firestoreDataSource.getFirestoreMatchById(matchId)?.toMatch()
    }

    private suspend fun FirestoreMatch.toMatch(): Match? {
        val userId = this.usersMatched.firstOrNull { it != authDataSource.userId } ?: return null
        val user = firestoreDataSource.getFirestoreUserModel(userId)
        val picture = storageDataSource.getPictureFromUser(userId, user.pictures.first())
        return Match(
            this.id,
            user.birthDate?.toAge() ?: 99,
            userId,
            user.name,
            picture.uri,
            this.timestamp?.toShortString() ?: "",
            this.lastMessage
        )
    }

    override fun getMessages(matchId: String) = firestoreDataSource.getMessages(matchId)

    override suspend fun sendMessage(matchId: String, text: String) {
        firestoreDataSource.sendMessage(matchId, text)
    }

    override suspend fun sendGiphyGif(matchId: String, giphyMediaId: String) {
        firestoreDataSource.sendGiphyGif(matchId, giphyMediaId)
    }

    override suspend fun likeMessage(matchId: String, messageId: String) {
        firestoreDataSource.likeMessage(matchId, messageId)
    }

    override suspend fun unLikeMessage(matchId: String, messageId: String) {
        firestoreDataSource.unLikeMessage(matchId, messageId)
    }
}