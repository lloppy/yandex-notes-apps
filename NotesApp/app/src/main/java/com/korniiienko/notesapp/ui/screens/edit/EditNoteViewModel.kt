package com.korniiienko.notesapp.ui.screens.edit

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.korniiienko.domain.LocalRepository
import com.korniiienko.domain.RemoteRepository
import com.korniiienko.notesapp.ui.screens.NoteEntity
import com.korniiienko.notesapp.ui.screens.toNote
import com.korniiienko.notesapp.ui.screens.toUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import okhttp3.Dispatcher

class EditNoteViewModel(
    savedStateHandle: SavedStateHandle,
    private val remoteRepository: RemoteRepository,
    private val localRepository: LocalRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(EditNoteState())
    val state: StateFlow<EditNoteState> = _state

    fun processIntent(intent: EditNoteIntent) {
        when (intent) {
            is EditNoteIntent.LoadNote -> loadNote(intent.uid)
            is EditNoteIntent.UpdateNoteEntity -> updateNoteState(intent.note)
            is EditNoteIntent.UpdateNote -> updateNote()
            is EditNoteIntent.DeleteNote -> deleteNote()
            is EditNoteIntent.ResetDeleteState -> resetDeleteState()
        }
    }

    private fun loadNote(uid: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            localRepository.getNoteByUid(uid = uid)
                .filterNotNull()
                .map { note ->
                    _state.value.copy(
                        currentNote = note.toUiState(),
                        isEntryValid = true,
                        isLoading = false
                    )
                }
                .first()
                .let { newState ->
                    _state.value = newState
                }
        }
    }

    private fun updateNoteState(newNote: NoteEntity) {
        _state.value = _state.value.copy(
            currentNote = newNote,
            isEntryValid = validateInput(newNote)
        )
    }

    private fun validateInput(uiEntry: NoteEntity = _state.value.currentNote): Boolean {
        return with(uiEntry) {
            title.isNotBlank() && content.isNotBlank()
        }
    }

    private fun updateNote() {
        viewModelScope.launch(Dispatchers.IO + SupervisorJob()) {
            if (validateInput()) {
                val newNote = _state.value.currentNote.toNote()

                remoteRepository.updateNote(note = newNote)
                    .onSuccess {
                        localRepository.updateNote(note = newNote )
                    }
                    .onFailure {
                        Log.e("EditNoteViewModel", "error occured: ${it.localizedMessage}")
                    }
            }
        }
    }

    private fun deleteNote() {
        viewModelScope.launch {
            val noteUid = _state.value.currentNote.toNote().uid

            remoteRepository.deleteNote(uid = noteUid)
                .onSuccess {
                    localRepository.deleteNote(uid = noteUid )
                    _state.value = _state.value.copy(isDeleted = true)
                }
                .onFailure {
                    Log.e("EditNoteViewModel", "error occured: ${it.localizedMessage}")
                }
        }
    }

    private fun resetDeleteState() {
        _state.value = _state.value.copy(isDeleted = false)
    }
}