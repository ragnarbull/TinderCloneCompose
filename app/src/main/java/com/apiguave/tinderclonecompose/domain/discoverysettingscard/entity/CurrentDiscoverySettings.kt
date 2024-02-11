package com.apiguave.tinderclonecompose.domain.discoverysettingscard.entity

import com.apiguave.tinderclonecompose.data.datasource.model.FirestoreUserProperties

data class CurrentDiscoverySettings(
    val maxDistance: Int = 2,
    val minAge: Int = 100,
    val maxAge: Int = 100
    ){

    fun isDataEqual(newMaxDistance: Int,
                    newMinAge: Int,
                    newMaxAge: Int
    ): Boolean {
        return newMaxDistance == this.maxDistance &&
                newMinAge == this.minAge &&
                newMaxAge == this.maxAge
    }

    fun toModifiedDiscoverySettings(newMaxDistance: Int = this.maxDistance,
                          newMinAge: Int = this.minAge,
                          newMaxAge: Int = this.maxAge
                          ): CurrentDiscoverySettings {
        return this.copy(
            maxDistance = if(newMaxDistance != this.maxDistance) newMaxDistance else this.maxDistance,
            minAge = if(newMinAge != this.minAge) newMinAge else this.minAge,
            maxAge = if(newMaxAge != this.maxAge) newMaxAge else this.maxAge
            )
    }

    fun toModifiedData(newMaxDistance: Int,
                       newMinAge: Int,
                       newMaxAge: Int,
    ): Map<String, Any> {
        val data = mutableMapOf<String, Any>()
        if(newMaxDistance != this.maxDistance){
            data[FirestoreUserProperties.maxDistance] = newMaxDistance
        }
        if(newMinAge != this.minAge){
            data[FirestoreUserProperties.minAge] = newMinAge
        }
        if(newMaxAge != this.maxAge){
            data[FirestoreUserProperties.maxAge] = newMaxAge
        }
        return data
    }
}
