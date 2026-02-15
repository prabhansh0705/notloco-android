package com.notloco.android.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notloco.android.data.models.Resource
import com.notloco.android.data.models.UiState
import com.notloco.android.data.models.UserInfo
import com.notloco.android.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _profileState = MutableStateFlow<UiState<UserInfo>>(UiState.Loading)
    val profileState: StateFlow<UiState<UserInfo>> = _profileState.asStateFlow()

    init {
        fetchProfile()
    }

    fun fetchProfile() {
        viewModelScope.launch {
            authRepository.getCurrentUserDetail().collect { resource ->
                when (resource) {
                    is Resource.Loading -> _profileState.value = UiState.Loading
                    is Resource.Success -> {
                        resource.data?.let {
                            _profileState.value = UiState.Success(it)
                        } ?: run {
                            _profileState.value = UiState.Error("No profile data received")
                        }
                    }
                    is Resource.Error -> {
                        _profileState.value = UiState.Error(resource.message ?: "Failed to load profile")
                    }
                }
            }
        }
    }
}
