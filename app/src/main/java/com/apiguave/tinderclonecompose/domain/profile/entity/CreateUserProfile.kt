package com.apiguave.tinderclonecompose.domain.profile.entity

import android.graphics.Bitmap
import java.time.LocalDate

data class CreateUserProfile(
    val name: String,
    val birthdate: LocalDate,
    val bio: String,
    val isMale: Boolean,
    val location: UserLocation,
    val orientation: Orientation,
    // Discovery Settings
    val maxDistance: Int,
    val minAge: Int,
    val maxAge: Int,
    // Essentials
    val height: String,
    val jobTitle: String,
    val languages: String,
    // Basics
    val zodiacSign: String,
    val education: String,
    // Interests
    val interests: String,
    // Pictures
    val pictures: List<Bitmap>,
)