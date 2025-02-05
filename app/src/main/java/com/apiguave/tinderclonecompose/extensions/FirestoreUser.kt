package com.apiguave.tinderclonecompose.extensions

import android.net.Uri
import com.apiguave.tinderclonecompose.domain.profilecard.entity.CurrentProfile
import com.apiguave.tinderclonecompose.data.datasource.model.FirestoreUser
import com.apiguave.tinderclonecompose.domain.profilecard.entity.Profile
import com.apiguave.tinderclonecompose.domain.profile.entity.FirebasePicture

fun FirestoreUser.toProfile(uris: List<Uri>): Profile {
    return Profile(
        this.id,
        this.name,
        this.birthDate?.toAge() ?: 99,
        this.bio,
        this.height,
        this.jobTitle,
        this.languages,
        this.zodiacSign,
        this.education,
        this.interests,
        uris,
        )
}

fun FirestoreUser.toCurrentProfile(uris: List<FirebasePicture>): CurrentProfile {
    return CurrentProfile(
        this.id,
        this.name,
        this.birthDate?.toLongString() ?: "",
        this.bio,
        this.male?.let { if(it) 0 else 1 } ?: -1,
        this.orientation?.ordinal ?: -1,
        this.height,
        this.jobTitle,
        this.languages,
        this.zodiacSign,
        this.education,
        this.interests,
        uris
    )
}