package com.apiguave.tinderclonecompose.ui.home

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apiguave.tinderclonecompose.domain.profilecard.ProfileCardRepository
import com.apiguave.tinderclonecompose.domain.profile.ProfileRepository
import com.apiguave.tinderclonecompose.domain.profile.entity.UserLocation
import com.apiguave.tinderclonecompose.domain.profilecard.entity.CurrentProfile
import com.apiguave.tinderclonecompose.domain.profilecard.entity.NewMatch
import com.apiguave.tinderclonecompose.domain.profilecard.entity.Profile
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomeViewModel(
    private val appContext: Application,
    private val profileRepository: ProfileRepository,
    private val profileCardRepository: ProfileCardRepository
): ViewModel() {
    private val TAG = "HomeViewModel"
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _currentProfile = MutableSharedFlow<CurrentProfile>()
    val currentProfile = _currentProfile.asSharedFlow()

    private val _newMatch = MutableSharedFlow<NewMatch>()
    val newMatch = _newMatch.asSharedFlow()

    // Private variable to store the last removed profile
    private var lastRemovedProfile: Profile? = null

    init {
        getLastKnownLocationAndFetchProfiles()
    }

    fun getLastKnownLocationAndFetchProfiles() {
        // initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(appContext)
        viewModelScope.launch {
            if (ContextCompat.checkSelfPermission(
                    appContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    appContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                try {
                    val location: Location? = fusedLocationClient.lastLocation.await()
                    location?.let {
                        Log.d(TAG, "Latitude: ${it.latitude}, Longitude: ${it.longitude}")
                        val currentUserLocation = UserLocation(it.latitude, it.longitude)
                        updateUserLocation(currentUserLocation)
                        fetchProfiles()
                    } ?: run {
                        Log.d(TAG, "Last known location is null")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error getting last known location: ${e.message}", e)
                    Toast.makeText(appContext, "Error getting location", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.d(TAG, "Location permission not granted")
                Toast.makeText(appContext, "Location permission not granted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun updateUserLocation(currentUserLocation: UserLocation) {
        try {
            val previousSavedLocation = profileRepository.getSavedUserLocation()
            val updatedUserLocation = profileRepository.updateUserLocation(previousSavedLocation, currentUserLocation)

            Log.d(TAG, "Previous saved location: $previousSavedLocation")
            Log.d(TAG, "Updated user location: $updatedUserLocation")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user location: ${e.message}", e)
        }
    }

    private suspend fun fetchProfiles(){
        try {
            _uiState.value = HomeUiState.Searching // Update UI state to indicate searching
            Log.d(TAG, "Fetching new profiles...")
            val profileList = profileCardRepository.getProfiles()
            _uiState.value = HomeUiState.Success(profileList.profiles) // Update UI state with fetched profiles
            _currentProfile.emit(profileList.currentProfile)
        } catch (e: Exception){
            Log.e(TAG, "Error fetching compatible profiles: ${e.message}", e)
            _uiState.value = HomeUiState.Error(e.message) // Update UI state with error message
        }
    }

    fun swipeUserAction(profile: Profile, swipeDirection: Int) {
        viewModelScope.launch {
            try {
                if (swipeDirection != 0) {
                    Log.d(TAG, "Swipe direction valid")
                    val isLike = swipeDirection == 2 || swipeDirection == 3 // TODO: add specific logic for super like
                    Log.d(TAG, "Swiping user: ${profile.name}, isLike: $isLike")

                    val match = profileCardRepository.swipeUser(profile, isLike)

                    lastRemovedProfile = if (match != null){
                        Log.d(TAG, "Match found: ${match.userName}")
                        _newMatch.emit(match)
                        null // Prevent the user from undoing the match via undoSwipe
                    } else {
                        profile // Store the last removed profile
                    }
                } else {
                    Log.d(TAG, "Swipe direction invalid")
                }

            } catch (e: Exception){
                Log.e(TAG, "Error swiping user: ${e.message}", e)
                //Bringing the profile card back to the profile deck?
            }
        }
    }

    fun removeLastProfile(){
        _uiState.update {
            if(it is HomeUiState.Success){
                it.copy(profileList = it.profileList.dropLast(1))
            } else it
        }
    }

    private fun undoSwipeUi(){
        _uiState.update {
            if(it is HomeUiState.Success){
                val restoredProfileList = if (lastRemovedProfile != null) {
                    it.profileList.plus(lastRemovedProfile!!)
                } else {
                    it.profileList
                }
                lastRemovedProfile = null // Clear the last removed profile after restoring

                it.copy(profileList = restoredProfileList)
            } else it
        }
    }

    fun clearLastRemovedProfile() {
        lastRemovedProfile = null
    }

    fun setLoading(){
        _uiState.update { HomeUiState.Loading}
    }

    suspend fun undoSwipe(swipeDirection: Int) {
        try {
            if (lastRemovedProfile != null) {
                if (swipeDirection != 0) {
                    Log.d(TAG, "Undoing swipe: Swipe direction valid")
                    val success = profileCardRepository.undoSwipe(swipeDirection)
                    if (success) {
                        Log.d(TAG, "Undoing swipe data: success")
                        undoSwipeUi()
                    } else {
                        Log.d(TAG, "Undoing swipe data: fail")
                    }
                } else {
                    Log.d(TAG, "Undoing swipe: Swipe direction invalid")
                }
            } else {
                Log.d(TAG, "No last removed profile found")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error undoing swipe data: ${e.message}", e)
            _uiState.emit(HomeUiState.Error(e.message))
        }
    }

    fun superLike() {
        try {
            Log.d(TAG, "Super liking user...")
        } catch (e: Exception) {
            Log.e(TAG, "Error with super like: ${e.message}", e)
        }
    }

    fun boost() {
        try {
            Log.d(TAG, "Boosting current user...")
        } catch (e: Exception) {
            Log.e(TAG, "Error with boost: ${e.message}", e)
        }
    }
}

sealed class HomeUiState{

    data object Loading: HomeUiState()

    data object Searching: HomeUiState()

    data class Success(
        val profileList: List<Profile>
    ): HomeUiState()

    data class Error(val message: String?): HomeUiState()
}
