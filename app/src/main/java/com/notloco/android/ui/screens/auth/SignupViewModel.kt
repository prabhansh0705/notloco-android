package com.notloco.android.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notloco.android.data.models.Resource
import com.notloco.android.data.models.SignupResponse
import com.notloco.android.data.models.UiState
import com.notloco.android.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _signupState = MutableStateFlow<UiState<SignupResponse>>(UiState.Idle)
    val signupState: StateFlow<UiState<SignupResponse>> = _signupState.asStateFlow()

    fun signup(
        name: String,
        email: String,
        phoneNumber: String
    ) {
        viewModelScope.launch {
            authRepository.signup(name, email, phoneNumber).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _signupState.value = UiState.Loading
                    }
                    is Resource.Success -> {
                        resource.data?.let {
                            _signupState.value = UiState.Success(it)
                        } ?: run {
                            _signupState.value = UiState.Error("No data received")
                        }
                    }
                    is Resource.Error -> {
                        _signupState.value = UiState.Error(resource.message ?: "Signup failed")
                    }
                }
            }
        }
    }

    fun resetState() {
        _signupState.value = UiState.Idle
    }
}
