package com.apiguave.tinderclonecompose.ui.discoverysettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apiguave.tinderclonecompose.domain.discoverysettingscard.entity.CurrentDiscoverySettings
import com.apiguave.tinderclonecompose.domain.profile.ProfileRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DiscoverySettingsViewModel(
    private val profileRepository: ProfileRepository,
): ViewModel() {
    private val _uiState = MutableStateFlow(
        DiscoverySettingsUiState(
            CurrentDiscoverySettings(),
            false,
            null)
    )
    val uiState = _uiState.asStateFlow()

    private val _action = MutableSharedFlow<EditDiscoverySettingsAction>()
    val action = _action.asSharedFlow()

    suspend fun getDiscoverySettings() = profileRepository.getSavedDiscoverySettings()

    fun setCurrentDiscoverySettings(currentDiscoverySettings: CurrentDiscoverySettings){
        _uiState.update { it.copy(currentDiscoverySettings = currentDiscoverySettings) }
    }

    fun updateDiscoverySettings(
        currentDiscoverySettings: CurrentDiscoverySettings,
        newMaxDistance: Int,
        newMinAge: Int,
        newMaxAge: Int
    ){
        viewModelScope.launch {
            //Otherwise show loading and perform update operations
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try{
                val updatedDiscoverySettings = profileRepository.updateDiscoverySettings(
                    currentDiscoverySettings,
                    newMaxDistance,
                    newMinAge,
                    newMaxAge
                )
                setCurrentDiscoverySettings(updatedDiscoverySettings)
                _action.emit(EditDiscoverySettingsAction.ON_DISCOVERY_SETTINGS_EDITED)
            } catch (e: Exception){
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}

data class DiscoverySettingsUiState(
    val currentDiscoverySettings: CurrentDiscoverySettings,
    val isLoading: Boolean,
    val errorMessage: String? = null)

enum class EditDiscoverySettingsAction{ON_DISCOVERY_SETTINGS_EDITED}