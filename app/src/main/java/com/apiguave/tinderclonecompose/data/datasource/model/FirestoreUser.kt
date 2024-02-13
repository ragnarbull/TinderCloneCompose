package com.apiguave.tinderclonecompose.data.datasource.model

import com.apiguave.tinderclonecompose.domain.profile.entity.Orientation
import com.apiguave.tinderclonecompose.domain.profile.entity.UserLocation
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class FirestoreUser(
    @DocumentId
    var id: String="",
    val name: String="",
    val birthDate: Timestamp?=null,
    val bio: String="",
    @field:JvmField
    val male: Boolean?=null,
    val location: UserLocation? = null,
    val orientation: Orientation?=null,
    // Discovery Settings
    val maxDistance: Int=-1,
    val minAge: Int=-1,
    val maxAge: Int=-1,
    // Essentials
    val height: String="",
    val jobTitle: String="",
    val languages: String="",
    // Basics
    val zodiacSign: String="",
    val education: String="",
    // Interests
    val interests: String="",
    // Swiping history
    val liked: List<String> = emptyList(),
    val passed: List<String> = emptyList(),
    // Pictures
    val pictures: List<String> = emptyList()
)

object FirestoreUserProperties {
    // The property values should reflect the actual name of the data class
    // property they are referencing. This is done so in order to keep track
    // of the property names from a single place.
    const val bio = "bio"
    const val birthDate = "birthDate"
    const val isMale = "male"
    const val pictures = "pictures"
    const val location = "location"
    const val orientation = "orientation"
    // Discovery Settings
    const val maxDistance = "maxDistance"
    const val minAge = "minAge"
    const val maxAge = "maxAge"
    // Essentials
    const val height = "height"
    const val jobTitle = "jobTitle"
    const val languages = "languages"
    // Basics
    const val zodiacSign = "zodiacSign"
    const val education = "education"
    // Interests
    const val interests = "interests"
    // Swiping history
    const val liked = "liked"
    const val passed = "passed"
}
