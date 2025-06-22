package com.yuaatn.instagram_notes.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yuaatn.instagram_notes.data.sync.NotesSynchronizer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: NotesSynchronizer
) : ViewModel() {

    val uiState = repository.notes
        .map { HomeState.Success(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = NOT_SHUTDOWN_DELAY),
            initialValue = HomeState.Loading
        )

    fun processAction(action: HomeAction) {
        when (action) {
            is HomeAction.InitializeHome -> fetchNotes()
            is HomeAction.RemoveNote -> removeNoteById(action.noteUid)
        }
    }

    private fun fetchNotes() {
        viewModelScope.launch {
            repository.synchronize()
        }
    }

    private fun removeNoteById(uid: String) {
        viewModelScope.launch {
            repository.syncOnDelete(uid)
        }
    }

    companion object {
        private const val NOT_SHUTDOWN_DELAY = 3_000L
    }
}
