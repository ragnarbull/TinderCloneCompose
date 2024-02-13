package com.apiguave.tinderclonecompose.data.repository

import android.net.Uri
import com.apiguave.tinderclonecompose.data.datasource.AuthRemoteDataSource
import com.apiguave.tinderclonecompose.data.datasource.FirestoreRemoteDataSource
import com.apiguave.tinderclonecompose.data.datasource.StorageRemoteDataSource
import com.apiguave.tinderclonecompose.data.datasource.model.FirestoreUserProperties
import com.apiguave.tinderclonecompose.domain.discoverysettingscard.entity.CurrentDiscoverySettings
import com.apiguave.tinderclonecompose.domain.profile.ProfileRepository
import com.apiguave.tinderclonecompose.domain.profile.entity.CreateUserProfile
import com.apiguave.tinderclonecompose.domain.profilecard.entity.CurrentProfile
import com.apiguave.tinderclonecompose.domain.profile.entity.FirebasePicture
import com.apiguave.tinderclonecompose.domain.profile.entity.UserLocation
import com.apiguave.tinderclonecompose.domain.profile.entity.UserPicture

class ProfileRepositoryImpl(
    private val authDataSource: AuthRemoteDataSource,
    private val storageDataSource: StorageRemoteDataSource,
    private val firestoreDataSource: FirestoreRemoteDataSource
): ProfileRepository {

    override suspend fun createUserProfile(profile: CreateUserProfile) {
        createUserProfile(authDataSource.userId, profile)
    }

    override suspend fun createUserProfile(userId: String, profile: CreateUserProfile) {
        val filenames = storageDataSource.uploadUserPictures(userId, profile.pictures)
        firestoreDataSource.createUserProfile(
            userId,
            profile.name,
            profile.birthdate,
            profile.bio,
            profile.isMale,
            profile.location,
            profile.orientation,
            profile.maxDistance,
            profile.minAge,
            profile.maxAge,
            profile.height,
            profile.jobTitle,
            profile.languages,
            profile.zodiacSign,
            profile.education,
            profile.interests,
            filenames.map { it.filename }
        )
    }

    override suspend fun updateProfile(
        currentProfile: CurrentProfile,
        newBio: String,
        newGenderIndex: Int,
        newOrientationIndex: Int,
        newHeight: String,
        newJobTitle: String,
        newLanguages: String,
        newZodiacSign: String,
        newEducation: String,
        newInterests: String,
        newPictures: List<UserPicture>
        ): CurrentProfile {

        val arePicturesEqual = currentProfile.pictures == newPictures

        val isDataEqual = currentProfile.isDataEqual(
            newBio,
            newGenderIndex,
            newOrientationIndex,
            newHeight,
            newJobTitle,
            newLanguages,
            newZodiacSign,
            newEducation,
            newInterests
        )

        if (arePicturesEqual && isDataEqual) {
            return currentProfile
        } else if (arePicturesEqual) {
            val data = currentProfile.toModifiedData(
                newBio,
                newGenderIndex,
                newOrientationIndex,
                newHeight,
                newJobTitle,
                newLanguages,
                newZodiacSign,
                newEducation,
                newInterests
            )
            firestoreDataSource.updateProfileData(data)
            return currentProfile.toModifiedProfile(
                newBio,
                newGenderIndex,
                newOrientationIndex,
                newHeight,
                newJobTitle,
                newLanguages,
                newZodiacSign,
                newEducation,
                newInterests
            )
        } else if (isDataEqual) {
            val firebasePictures = updateProfilePictures(currentProfile.pictures, newPictures)
            return currentProfile.copy(pictures = firebasePictures)
        } else {
            val data = currentProfile.toModifiedData(
                newBio,
                newGenderIndex,
                newOrientationIndex,
                newHeight,
                newJobTitle,
                newLanguages,
                newZodiacSign,
                newEducation,
                newInterests
            )
            val firebasePictures =
                updateProfileDataAndPictures(data, currentProfile.pictures, newPictures)
            return currentProfile.toModifiedProfile(
                newBio,
                newGenderIndex,
                newOrientationIndex,
                newHeight,
                newJobTitle,
                newLanguages,
                newZodiacSign,
                newEducation,
                newInterests,
                firebasePictures
            )
        }
    }

    private suspend fun updateProfilePictures(
        outdatedPictures: List<FirebasePicture>,
        updatedPictures: List<UserPicture>
    ): List<FirebasePicture> {
        val filenames = storageDataSource.updateProfilePictures(
            authDataSource.userId,
            outdatedPictures,
            updatedPictures
        )
        val updatedData =
            mapOf<String, Any>(FirestoreUserProperties.pictures to filenames.map { it.filename })
        firestoreDataSource.updateProfileData(updatedData)
        return filenames
    }

    private suspend fun updateProfileDataAndPictures(
        data: Map<String, Any>,
        outdatedPictures: List<FirebasePicture>,
        updatedPictures: List<UserPicture>
    ): List<FirebasePicture> {
        val filenames = storageDataSource.updateProfilePictures(
            authDataSource.userId,
            outdatedPictures,
            updatedPictures
        )
        val updatedData =
            data + mapOf<String, Any>(FirestoreUserProperties.pictures to filenames.map { it.filename })
        firestoreDataSource.updateProfileData(updatedData)
        return filenames
    }

    override suspend fun getSavedDiscoverySettings(): CurrentDiscoverySettings {
        return firestoreDataSource.getSavedDiscoverySettings()
    }

    override suspend fun updateDiscoverySettings(
        currentDiscoverySettings: CurrentDiscoverySettings,
        newMaxDistance: Int,
        newMinAge: Int,
        newMaxAge: Int
    ): CurrentDiscoverySettings {

        val isDataEqual = currentDiscoverySettings.isDataEqual(newMaxDistance, newMinAge, newMaxAge)

        return if (isDataEqual) {
            currentDiscoverySettings
        } else {
            val data = currentDiscoverySettings.toModifiedData(newMaxDistance, newMinAge, newMaxAge)
            firestoreDataSource.updateProfileData(data)
            currentDiscoverySettings.toModifiedDiscoverySettings(
                newMaxDistance,
                newMinAge,
                newMaxAge
            )
        }
    }

    override suspend fun getSavedUserLocation(): UserLocation {
        return firestoreDataSource.getSavedLocation()
    }

    override suspend fun updateUserLocation(
        previousUserLocation: UserLocation,
        newUserLocation: UserLocation
    ): UserLocation {

        val data = mutableMapOf<String, Any>()
        data[FirestoreUserProperties.location] = newUserLocation
        firestoreDataSource.updateProfileData(data)
        return newUserLocation
    }

    override suspend fun fetchCurrentUserProfilePic(): Uri {
        val user = firestoreDataSource.fetchCurrentUser()
        val picture = storageDataSource.getPictureFromUser(user.id, user.pictures.first())
        return picture.uri
    }
}