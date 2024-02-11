package com.apiguave.tinderclonecompose.domain.profile

import android.net.Uri
import com.apiguave.tinderclonecompose.domain.discoverysettingscard.entity.CurrentDiscoverySettings
import com.apiguave.tinderclonecompose.domain.profile.entity.CreateUserProfile
import com.apiguave.tinderclonecompose.domain.profile.entity.FullProfile
import com.apiguave.tinderclonecompose.domain.profile.entity.UserLocation
import com.apiguave.tinderclonecompose.domain.profilecard.entity.CurrentProfile
import com.apiguave.tinderclonecompose.domain.profile.entity.UserPicture

interface ProfileRepository {
    suspend fun createUserProfile(profile: CreateUserProfile)
    suspend fun createUserProfile(userId: String, profile: CreateUserProfile)
    suspend fun updateProfile(currentProfile: CurrentProfile, newBio: String, newGenderIndex: Int, newOrientationIndex: Int, newPictures: List<UserPicture>): CurrentProfile
    suspend fun updateDiscoverySettings(currentDiscoverySettings: CurrentDiscoverySettings, newMaxDistance: Int, newMinAge: Int, newMaxAge: Int): CurrentDiscoverySettings
    suspend fun getSavedUserLocation(): UserLocation
    suspend fun updateUserLocation(previousUserLocation: UserLocation, newUserLocation: UserLocation): UserLocation
    suspend fun getSavedDiscoverySettings(): CurrentDiscoverySettings
    suspend fun fetchCurrentUserProfilePic(): Uri
    suspend fun getUserProfileById(userId: String): FullProfile
}