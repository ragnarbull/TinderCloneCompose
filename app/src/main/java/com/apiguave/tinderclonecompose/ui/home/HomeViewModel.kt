package com.apiguave.tinderclonecompose.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apiguave.tinderclonecompose.data.Profile
import com.apiguave.tinderclonecompose.data.getRandomUserId
import com.apiguave.tinderclonecompose.data.CreateUserProfile
import com.apiguave.tinderclonecompose.data.repository.FirebaseRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState(true, emptyList(), null))
    val uiState = _uiState.asStateFlow()

    init{ fetchProfiles() }

    fun swipeUser(userId: String, isLike: Boolean){
        viewModelScope.launch {
            try {
                val isMatch = FirebaseRepository.swipeUser(userId, isLike)
            }catch (e: Exception){
                //Bringing the profile card back to the profile deck?
            }
        }
    }

    fun removeLastProfile(){
        _uiState.update { it.copy(profileList = it.profileList.dropLast(1)) }
    }

    fun setLoading(isLoading: Boolean){
        _uiState.update { it.copy(isLoading = isLoading, errorMessage = null) }
    }
    fun createProfiles(profiles: List<CreateUserProfile>){
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                profiles.map { async { FirebaseRepository.createUserProfile(getRandomUserId(),  it) } }.awaitAll()
                fetchProfiles()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun fetchProfiles(){
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val profileList = FirebaseRepository.getProfiles()
                _uiState.update { it.copy(isLoading = false, profileList = profileList) }
            }catch (e: Exception){
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

}

data class HomeUiState(
    val isLoading: Boolean,
    val profileList: List<Profile>,
    val errorMessage: String?)