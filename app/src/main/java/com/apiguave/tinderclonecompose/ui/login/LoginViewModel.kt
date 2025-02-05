package com.apiguave.tinderclonecompose.ui.login

import android.util.Log
import android.util.Log.getStackTraceString
import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apiguave.tinderclonecompose.data.datasource.SignInCheck
import com.apiguave.tinderclonecompose.domain.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: AuthRepository): ViewModel() {

    companion object {
        private const val TAG = "LoginViewModel"
    }

    private val _uiState = MutableStateFlow(
        LoginViewState(
            isLoading = true,
            isUserSignedIn = false,
            errorMessage = null
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        checkLoginState()
    }

    private fun checkLoginState() {
        _uiState.update {
            if(authRepository.isUserSignedIn){
                it.copy(isUserSignedIn = true)
            } else {
                it.copy(isLoading = false)
            }
        }
    }

    fun signIn(activityResult: ActivityResult){
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                Log.d(TAG, "Attempting sign-in...")
                authRepository.signInWithGoogle(activityResult.data, signInCheck = SignInCheck.ENFORCE_EXISTING_USER)
                Log.d(TAG, "Sign-in successful")
                _uiState.update { it.copy(isUserSignedIn = true) }
            } catch (e: Exception) {
                Log.e(TAG, "Sign-in failed: ${e.message}\n${getStackTraceString(e)}")
                if(authRepository.isUserSignedIn){
                    authRepository.signOut()
                }
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message)
                }
            }
        }
    }
}

data class LoginViewState(val isLoading: Boolean, val isUserSignedIn: Boolean, val errorMessage: String?)