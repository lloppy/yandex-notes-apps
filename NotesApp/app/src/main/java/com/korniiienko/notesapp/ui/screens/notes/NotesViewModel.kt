package com.korniiienko.notesapp.ui.screens.notes

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.korniiienko.domain.LocalRepository
import com.korniiienko.domain.RemoteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotesViewModel(
    private val remoteRepository: RemoteRepository,
    private val localRepository: LocalRepository,
) : ViewModel() {

    val uiState: StateFlow<NotesState> =
        localRepository.notes.map {
            NotesState.Success(it)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = TIMEOUT),
            initialValue = NotesState.Loading
        )

    fun processIntent(intent: NotesIntent) {
        when (intent) {
            is NotesIntent.LoadNotes -> loadNotes()
            is NotesIntent.DeleteNote -> deleteNote(intent.noteId)
            is NotesIntent.DeleteAllFromServer -> deleteAllFromServer()
            is NotesIntent.SyncFromServer -> syncFromServer()
            is NotesIntent.GetFromServer -> getNotesFromServer()
        }
    }

    private fun getNotesFromServer(){
        viewModelScope.launch {
            remoteRepository.getNotes()
                .onSuccess { result ->
                    result.forEach { note ->
                        localRepository.addNote(note)
                    }
                }
                .onFailure {
                    Log.e("NotesViewModel", "error occured: ${it.localizedMessage}")
                }
        }
    }

    private fun deleteAllFromServer(){
        viewModelScope.launch {
            remoteRepository.clearAllNotes()
            localRepository.deleteAll()
        }
    }

    private fun syncFromServer(){
        viewModelScope.launch {
            remoteRepository.getNotes()
                .onSuccess { result ->
                    result.forEach { note ->
                        localRepository.updateNote(note)
                    }
                }
                .onFailure {
                    Log.e("NotesViewModel", "error occured: ${it.localizedMessage}")
                }
        }
    }

    private fun loadNotes() {
        viewModelScope.launch {
            localRepository.load()
        }
    }

    private fun deleteNote(noteUid: String) {
        viewModelScope.launch {
            remoteRepository.deleteNote(uid = noteUid)
                .onSuccess {
                    localRepository.deleteNote(uid = noteUid)
                }
                .onFailure {
                    Log.e("NotesViewModel", "error occured: ${it.localizedMessage}")
                }
        }
    }

    companion object {
        const val TIMEOUT = 3_000L
    }
}