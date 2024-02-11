package com.apiguave.tinderclonecompose.domain.profile.entity

// This is the "geopoint" type in Firestore
data class UserLocation(
    val latitude: Double = 0.0000,
    val longitude: Double = 0.0000
)