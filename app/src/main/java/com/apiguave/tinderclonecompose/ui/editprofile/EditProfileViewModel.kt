package com.apiguave.tinderclonecompose.ui.editprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apiguave.tinderclonecompose.domain.auth.AuthRepository
import com.apiguave.tinderclonecompose.domain.profile.ProfileRepository
import com.apiguave.tinderclonecompose.domain.profilecard.entity.CurrentProfile
import com.apiguave.tinderclonecompose.domain.profile.entity.DevicePicture
import com.apiguave.tinderclonecompose.domain.profile.entity.UserPicture
import com.apiguave.tinderclonecompose.extensions.filterIndex
import com.apiguave.tinderclonecompose.extensions.getTaskResult
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(
        EditProfileUiState(
            CurrentProfile(),
            false,
            emptyList(),
            null)
    )
    val uiState = _uiState.asStateFlow()

    private val _action = MutableSharedFlow<EditProfileAction>()
    val action = _action.asSharedFlow()

    fun setCurrentProfile(currentProfile: CurrentProfile){
        _uiState.update { it.copy(currentProfile = currentProfile, pictures = currentProfile.pictures) }
    }

    fun updateProfile(
        currentProfile: CurrentProfile,
        uiBio: String,
        uiGenderIndex: Int,
        uiOrientationIndex: Int,
        uiHeight: String,
        uiJobTitle: String,
        uiLanguages: String,
        uiZodiacSign: String,
        uiEducation: String,
        uiInterests: String,
        uiPictures: List<UserPicture>
    ){
        viewModelScope.launch {
            //Otherwise show loading and perform update operations
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try{
                val updatedProfile = profileRepository.updateProfile(
                    currentProfile,
                    uiBio,
                    uiGenderIndex,
                    uiOrientationIndex,
                    uiHeight,
                    uiJobTitle,
                    uiLanguages,
                    uiZodiacSign,
                    uiEducation,
                    uiInterests,
                    uiPictures
                )
                _uiState.update { it.copy(isLoading = false, currentProfile = updatedProfile, pictures = updatedProfile.pictures) }
                _action.emit(EditProfileAction.ON_PROFILE_EDITED)
            }catch (e: Exception){
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun addPicture(picture: DevicePicture){
        _uiState.update { it.copy(pictures = it.pictures + picture) }
    }

    fun removePictureAt(index: Int){
        _uiState.update { it.copy(pictures = it.pictures.filterIndex(index)) }
    }

    fun signOut(signInClient: GoogleSignInClient){
        viewModelScope.launch {
            authRepository.signOut()
            signInClient.signOut().getTaskResult()
            _action.emit(EditProfileAction.ON_SIGNED_OUT)
        }
    }

}

data class EditProfileUiState(
    val currentProfile: CurrentProfile,
    val isLoading: Boolean,
    val pictures: List<UserPicture>,
    val errorMessage: String? = null)

enum class EditProfileAction{ON_SIGNED_OUT, ON_PROFILE_EDITED}