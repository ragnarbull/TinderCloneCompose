package com.apiguave.tinderclonecompose.data.repository

import com.apiguave.tinderclonecompose.data.CreateUserProfile
import com.apiguave.tinderclonecompose.data.FirestoreUserModel
import com.apiguave.tinderclonecompose.data.Match
import com.apiguave.tinderclonecompose.data.Profile
import com.apiguave.tinderclonecompose.extensions.toAge
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

object FirebaseRepository {
    private val storageRepository = StorageRepository()
    private val firestoreRepository = FirestoreRepository()
    suspend fun createUserProfile(profile: CreateUserProfile) {
        createUserProfile(AuthRepository.userId, profile)
    }

    suspend fun createUserProfile(userId: String, profile: CreateUserProfile) {
        val filenames = storageRepository.uploadUserPictures(userId, profile.pictures)
        firestoreRepository.createUserProfile(userId, profile.name, profile.birthdate, profile.bio, profile.isMale, profile.orientation, filenames)
    }

    suspend fun getProfiles(): List<Profile>{
        val firestoreUserModels = firestoreRepository.getCompatibleUsers()
        //Fetch profiles
        val profiles = coroutineScope {
            firestoreUserModels.map {
                async{
                    getProfile(it)
                }

            }.awaitAll()
        }.filterNotNull()

        return profiles
    }

    private suspend fun getProfile(userModel: FirestoreUserModel): Profile?{
        val userId = userModel.id ?: return null
        if(userModel.pictures.isEmpty()) return null
        val uris = storageRepository.getUrisFromUser(userId, userModel.pictures)
        return Profile(userId, userModel.name ?: "", userModel.birthDate?.toAge() ?: 99, uris)
    }

    suspend fun getMatches(): List<Match>{
        val matchModels = firestoreRepository.getFirestoreMatchModels()
        val matches = coroutineScope {
            matchModels.mapNotNull { matchModel ->
                val matchId = matchModel.id ?: return@mapNotNull null
                async {
                    val userId = matchModel.usersMatched.first { it != AuthRepository.userId }
                    getMatch(matchId, userId)
                }
            }.awaitAll()
        }
        return matches
    }

    private suspend fun getMatch(matchId: String, userId: String): Match{
        val user = firestoreRepository.getFirestoreUserModel(userId)
        val uri = storageRepository.getUriFromUser(userId, user.pictures.first())
        return Match(matchId, user.birthDate?.toAge() ?: 99, userId, user.name?: "", uri,  null)
    }
}