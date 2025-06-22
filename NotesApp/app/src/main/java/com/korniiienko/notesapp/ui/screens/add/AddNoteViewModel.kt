package com.korniiienko.notesapp.ui.screens.add

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.korniiienko.domain.LocalRepository
import com.korniiienko.domain.RemoteRepository
import com.korniiienko.notesapp.ui.screens.NoteEntity
import com.korniiienko.notesapp.ui.screens.toNote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AddNoteViewModel(
    private val remoteRepository: RemoteRepository,
    private val localRepository: LocalRepository,
) : ViewModel() {
    var entryUiState by mutableStateOf(AddNoteState())
        private set

    fun processIntent(intent: AddNoteIntent) {
        when (intent) {
            is AddNoteIntent.UpdateNote -> updateUiState(intent.note)
            is AddNoteIntent.SaveNote -> saveNote()
        }
    }

    private fun updateUiState(newNote: NoteEntity) {
        entryUiState = AddNoteState(
            currentNote = newNote,
            isEntryValid = validateInput(newNote)
        )
    }

    private fun validateInput(uiState: NoteEntity = entryUiState.currentNote): Boolean {
        return with(uiState) {
            title.isNotBlank() && content.isNotBlank()
        }
    }

    private fun saveNote() {
        if (validateInput()) {
            viewModelScope.launch(Dispatchers.IO + SupervisorJob()) {
                val newNote = entryUiState.currentNote.toNote()

                remoteRepository.addNote(note = newNote)
                    .onSuccess {
                        localRepository.addNote(note = newNote)
                    }
                    .onFailure {
                        Log.e("AddNoteViewModel", "error occured: ${it.localizedMessage}")
                    }
            }
        }
    }
}