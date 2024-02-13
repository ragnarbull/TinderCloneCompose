package com.apiguave.tinderclonecompose.domain.profilecard.entity

import com.apiguave.tinderclonecompose.data.datasource.model.FirestoreUserProperties
import com.apiguave.tinderclonecompose.domain.profile.entity.FirebasePicture
import com.apiguave.tinderclonecompose.domain.profile.entity.Orientation

data class CurrentProfile(
    val id: String = "",
    val name: String = "",
    val birthDate: String = "",
    val bio: String = "",
    val genderIndex: Int = -1,
    val orientationIndex: Int = -1,
    // Essentials
    val height: String = "",
    val jobTitle: String = "",
    val languages: String = "",
    // Basics
    val zodiacSign: String = "",
    val education: String = "",
    // Interests
    val interests: String = "",
    // Pictures
    val pictures: List<FirebasePicture> = emptyList()
    ){

    fun isDataEqual(newBio: String,
                    newGenderIndex: Int,
                    newOrientationIndex: Int,
                    newHeight: String,
                    newJobTitle: String,
                    newLanguages: String,
                    newZodiacSign: String,
                    newEducation: String,
                    newInterests: String
    ): Boolean {
        return newBio == this.bio &&
                newGenderIndex == this.genderIndex &&
                newOrientationIndex == this.orientationIndex &&
                newHeight == this.height &&
                newJobTitle == this.jobTitle &&
                newLanguages == this.languages &&
                newZodiacSign == this.zodiacSign &&
                newEducation == this.education &&
                newInterests == this.interests
    }

    fun toModifiedProfile(newBio: String = this.bio,
                          newGenderIndex: Int = this.genderIndex,
                          newOrientationIndex: Int = this.orientationIndex,
                          newHeight: String = this.height,
                          newJobTitle: String = this.jobTitle,
                          newLanguages: String = this.languages,
                          newZodiacSign: String = this.zodiacSign,
                          newEducation: String = this.education,
                          newInterests: String = this.interests,
                          newPictures: List<FirebasePicture> = this.pictures
                          ): CurrentProfile {
        return this.copy(
            bio = if(newBio != this.bio) newBio else this.bio,
            genderIndex = if(newGenderIndex != this.genderIndex) newGenderIndex else this.genderIndex,
            orientationIndex = if(newOrientationIndex != this.orientationIndex) newOrientationIndex else this.orientationIndex,
            height = if(newHeight != this.height) newHeight else this.height,
            jobTitle = if(newJobTitle != this.jobTitle) newJobTitle else this.jobTitle,
            languages = if(newLanguages != this.languages) newLanguages else this.languages,
            zodiacSign = if(newZodiacSign != this.zodiacSign) newZodiacSign else this.zodiacSign,
            education = if(newEducation != this.education) newEducation else this.education,
            interests = if(newInterests != this.interests) newInterests else this.interests,
            pictures = if(newPictures != this.pictures) newPictures else this.pictures
            )
    }

    fun toModifiedData(newBio: String,
                       newGenderIndex: Int,
                       newOrientationIndex: Int,
                       newHeight: String,
                       newJobTitle: String,
                       newLanguages: String,
                       newZodiacSign: String,
                       newEducation: String,
                       newInterests: String
    ): Map<String, Any> {
        val data = mutableMapOf<String, Any>()
        if(newBio != this.bio){
            data[FirestoreUserProperties.bio] = newBio
        }
        if(newGenderIndex != this.genderIndex){
            data[FirestoreUserProperties.isMale] = newGenderIndex == 0
        }
        if(newOrientationIndex != this.orientationIndex){
            data[FirestoreUserProperties.orientation] = Orientation.entries.toTypedArray()[newOrientationIndex]
        }
        if(newHeight != this.height){
            data[FirestoreUserProperties.height] = newHeight
        }
        if(newJobTitle != this.jobTitle){
            data[FirestoreUserProperties.jobTitle] = newJobTitle
        }
        if(newLanguages != this.languages){
            data[FirestoreUserProperties.languages] = newLanguages
        }
        if(newZodiacSign != this.zodiacSign){
            data[FirestoreUserProperties.zodiacSign] = newZodiacSign
        }
        if(newEducation != this.education){
            data[FirestoreUserProperties.education] = newEducation
        }
        if(newInterests != this.interests){
            data[FirestoreUserProperties.interests] = newInterests
        }
        return data
    }
}