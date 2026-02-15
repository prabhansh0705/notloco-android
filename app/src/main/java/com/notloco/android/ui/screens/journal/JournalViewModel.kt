package com.notloco.android.ui.screens.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notloco.android.data.models.JournalListResponse
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
class JournalViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _journalState = MutableStateFlow<UiState<JournalListResponse>>(UiState.Loading)
    val journalState: StateFlow<UiState<JournalListResponse>> = _journalState.asStateFlow()

    init {
        fetchJournals()
    }

    fun fetchJournals(page: Int = 1) {
        viewModelScope.launch {
            authRepository.getJournalList(page).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _journalState.value = UiState.Loading
                    is Resource.Success -> {
                        resource.data?.let {
                            _journalState.value = UiState.Success(it)
                        } ?: run {
                            _journalState.value = UiState.Error("No journals received")
                        }
                    }
                    is Resource.Error -> {
                        _journalState.value = UiState.Error(resource.message ?: "Failed to load journals")
                    }
                }
            }
        }
    }
}
