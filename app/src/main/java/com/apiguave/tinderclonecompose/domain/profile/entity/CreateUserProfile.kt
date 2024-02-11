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
    val maxDistance: Int,
    val minAge: Int,
    val maxAge: Int,
    val pictures: List<Bitmap>,
)