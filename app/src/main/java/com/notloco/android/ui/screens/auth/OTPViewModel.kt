package com.notloco.android.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notloco.android.data.models.Resource
import com.notloco.android.data.models.UiState
import com.notloco.android.data.models.VerifyOTPResponse
import com.notloco.android.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OTPViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _otpState = MutableStateFlow<UiState<VerifyOTPResponse>>(UiState.Idle)
    val otpState: StateFlow<UiState<VerifyOTPResponse>> = _otpState.asStateFlow()

    private val _resendState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val resendState: StateFlow<UiState<Unit>> = _resendState.asStateFlow()

    fun verifyOtp(userId: Int, otp: String) {
        viewModelScope.launch {
            authRepository.verifyOtp(userId, otp).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _otpState.value = UiState.Loading
                    }
                    is Resource.Success -> {
                        resource.data?.let {
                            _otpState.value = UiState.Success(it)
                        } ?: run {
                            _otpState.value = UiState.Error("No data received")
                        }
                    }
                    is Resource.Error -> {
                        _otpState.value = UiState.Error(resource.message ?: "OTP verification failed")
                    }
                }
            }
        }
    }

    fun resendOtp(userId: Int) {
        viewModelScope.launch {
            authRepository.resendOtp(userId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _resendState.value = UiState.Loading
                    }
                    is Resource.Success -> {
                        _resendState.value = UiState.Success(Unit)
                    }
                    is Resource.Error -> {
                        _resendState.value = UiState.Error(resource.message ?: "Failed to resend OTP")
                    }
                }
            }
        }
    }

    fun resetState() {
        _otpState.value = UiState.Idle
        _resendState.value = UiState.Idle
    }
}
