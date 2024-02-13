package com.apiguave.tinderclonecompose.domain.profilecard.entity

import android.net.Uri

class Profile(
    val id: String,
    val name: String,
    val age: Int,
    val bio: String,
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
    val pictures: List<Uri>,
    )