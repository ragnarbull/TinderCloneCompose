package com.apiguave.tinderclonecompose.ui.editprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apiguave.tinderclonecompose.data.CurrentProfile
import com.apiguave.tinderclonecompose.data.DevicePicture
import com.apiguave.tinderclonecompose.data.UserPicture
import com.apiguave.tinderclonecompose.data.repository.AuthRepository
import com.apiguave.tinderclonecompose.data.repository.FirebaseRepository
import com.apiguave.tinderclonecompose.extensions.filterIndex
import com.apiguave.tinderclonecompose.extensions.getTaskResult
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class EditProfileViewModel: ViewModel() {
    private val _userPictures = MutableStateFlow<List<UserPicture>>(emptyList())
    val userPictures = _userPictures.asStateFlow()

    private val _action = MutableSharedFlow<EditProfileAction>()
    val action = _action.asSharedFlow()

    var currentProfile = CurrentProfile()
        set(value) {
            _userPictures.update { value.pictures }
            field = value
        }

    fun updateProfile(data: Map<String, Any>, pictures: List<UserPicture>){
        viewModelScope.launch {
            if(pictures.isNotEmpty() && data.isNotEmpty()){
                FirebaseRepository.updateProfileDataAndPictures(data, pictures)
            } else if(pictures.isEmpty() && data.isNotEmpty()){
                FirebaseRepository.updateProfileData(data)
            } else if(pictures.isNotEmpty()){
                FirebaseRepository.updateProfilePictures(pictures)
            }
            _action.emit(EditProfileAction.ON_PROFILE_EDITED)
        }
    }

    fun addPicture(picture: DevicePicture){
        _userPictures.update { it + picture }
    }

    fun removePictureAt(index: Int){
        _userPictures.update { it.filterIndex(index) }
    }

    fun signOut(signInClient: GoogleSignInClient){
        viewModelScope.launch {
            AuthRepository.signOut()
            signInClient.signOut().getTaskResult()
            _action.emit(EditProfileAction.ON_SIGNED_OUT)
        }
    }

}

enum class EditProfileAction{ON_SIGNED_OUT, ON_PROFILE_EDITED}