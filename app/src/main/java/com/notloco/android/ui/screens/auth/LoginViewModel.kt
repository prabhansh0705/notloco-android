package com.notloco.android.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notloco.android.data.models.LoginResponse
import com.notloco.android.data.models.Resource
import com.notloco.android.data.models.UiState
import com.notloco.android.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<UiState<LoginResponse>>(UiState.Idle)
    val loginState: StateFlow<UiState<LoginResponse>> = _loginState.asStateFlow()

    fun login(countryCode: String, mobile: String) {
        viewModelScope.launch {
            authRepository.login(countryCode, mobile).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _loginState.value = UiState.Loading
                    }
                    is Resource.Success -> {
                        resource.data?.let {
                            _loginState.value = UiState.Success(it)
                        } ?: run {
                            _loginState.value = UiState.Error("No data received")
                        }
                    }
                    is Resource.Error -> {
                        _loginState.value = UiState.Error(resource.message ?: "Login failed")
                    }
                }
            }
        }
    }

    fun resetState() {
        _loginState.value = UiState.Idle
    }
}
