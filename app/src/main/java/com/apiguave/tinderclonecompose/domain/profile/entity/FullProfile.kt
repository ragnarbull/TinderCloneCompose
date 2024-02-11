package com.apiguave.tinderclonecompose.domain.profile.entity

import android.net.Uri

class FullProfile(
    val id: String,
    val name: String,
    val age: Int,
    val pictures: List<Uri>,
    val bio: String
)