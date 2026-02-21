package com.notloco.android.ui.screens.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notloco.android.data.models.JournalEntry
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

    private val _journalState = MutableStateFlow<UiState<List<JournalEntry>>>(UiState.Loading)
    val journalState: StateFlow<UiState<List<JournalEntry>>> = _journalState.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private var currentPage = 1
    private var totalPages = 1
    private val allJournals = mutableListOf<JournalEntry>()
    private var isFetching = false

    val canLoadMore: Boolean
        get() = currentPage < totalPages

    init {
        fetchJournals()
    }

    fun fetchJournals() {
        currentPage = 1
        allJournals.clear()
        isFetching = false
        loadPage(page = 1, isInitial = true)
    }

    fun loadNextPage() {
        if (isFetching || !canLoadMore) return
        loadPage(page = currentPage + 1, isInitial = false)
    }

    private fun loadPage(page: Int, isInitial: Boolean) {
        isFetching = true
        viewModelScope.launch {
            if (isInitial) {
                _journalState.value = UiState.Loading
            } else {
                _isLoadingMore.value = true
            }

            authRepository.getJournalList(page).collect { resource ->
                when (resource) {
                    is Resource.Loading -> { }
                    is Resource.Success -> {
                        resource.data?.let { response ->
                            currentPage = page
                            totalPages = response.totalPages ?: 1

                            val newEntries = response.transcriptions.orEmpty()
                            if (isInitial) {
                                allJournals.clear()
                            }
                            allJournals.addAll(newEntries)
                            _journalState.value = UiState.Success(allJournals.toList())
                        } ?: run {
                            if (isInitial) {
                                _journalState.value = UiState.Error("No journals received")
                            }
                        }
                        _isLoadingMore.value = false
                        isFetching = false
                    }
                    is Resource.Error -> {
                        if (isInitial) {
                            _journalState.value = UiState.Error(resource.message ?: "Failed to load journals")
                        }
                        _isLoadingMore.value = false
                        isFetching = false
                    }
                }
            }
        }
    }
}
