package com.apiguave.tinderclonecompose.ui.viewuserprofile

import androidx.lifecycle.ViewModel
import com.apiguave.tinderclonecompose.domain.profilecard.entity.Profile
import kotlinx.coroutines.flow.*

class ViewUserProfileViewModel: ViewModel() {

    private val _userProfile: MutableStateFlow<Profile?> = MutableStateFlow(null)
    val userProfile = _userProfile.asStateFlow()

    fun setUserProfile(userProfile: Profile){
        _userProfile.value = userProfile
    }
}
