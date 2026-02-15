package com.notloco.android.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notloco.android.data.models.ChatListResponse
import com.notloco.android.data.models.CoachDetails
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
class ChatViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _chatState = MutableStateFlow<UiState<ChatListResponse>>(UiState.Loading)
    val chatState: StateFlow<UiState<ChatListResponse>> = _chatState.asStateFlow()
    private val _coachState = MutableStateFlow<UiState<CoachDetails>>(UiState.Loading)
    val coachState: StateFlow<UiState<CoachDetails>> = _coachState.asStateFlow()

    init {
        fetchChats()
        fetchCoachDetails()
    }

    fun fetchChats(isPinned: Boolean? = null) {
        viewModelScope.launch {
            authRepository.getUserChats(isPinned).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _chatState.value = UiState.Loading
                    is Resource.Success -> {
                        resource.data?.let {
                            _chatState.value = UiState.Success(it)
                        } ?: run {
                            _chatState.value = UiState.Error("No chats received")
                        }
                    }
                    is Resource.Error -> {
                        _chatState.value = UiState.Error(resource.message ?: "Failed to load chats")
                    }
                }
            }
        }
    }

    fun fetchCoachDetails() {
        viewModelScope.launch {
            authRepository.getUserCoach().collect { resource ->
                when (resource) {
                    is Resource.Loading -> _coachState.value = UiState.Loading
                    is Resource.Success -> {
                        resource.data?.let {
                            _coachState.value = UiState.Success(it)
                        } ?: run {
                            _coachState.value = UiState.Error("No coach details received")
                        }
                    }
                    is Resource.Error -> {
                        _coachState.value = UiState.Error(resource.message ?: "Failed to load coach details")
                    }
                }
            }
        }
    }
}
