package com.yuaatn.instagram_notes.ui.screens.add

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yuaatn.instagram_notes.data.sync.NotesSynchronizer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteCreationViewModel @Inject constructor(
    private val repository: NotesSynchronizer
) : ViewModel() {

    var uiState by mutableStateOf(NoteCreationState())
        private set

    fun processAction(action: NoteAction) {
        when (action) {
            is NoteAction.ModifyNote -> updateNoteState(action.note)
            NoteAction.CommitNote -> commitNote()
        }
    }

    private fun updateNoteState(note: NoteEntity) {
        uiState = uiState.copy(
            currentNote = note,
            isValid = checkNoteValidity(note)
        )
    }

    private fun checkNoteValidity(note: NoteEntity): Boolean {
        return note.title.trim().isNotEmpty() && note.content.trim().isNotEmpty()
    }

    private fun commitNote() {
        viewModelScope.launch {
            if (checkNoteValidity(uiState.currentNote)) {
                repository.syncOnCreate(uiState.currentNote.toNote())
            }
        }
    }
}