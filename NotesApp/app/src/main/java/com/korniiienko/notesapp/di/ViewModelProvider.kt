package com.korniiienko.notesapp.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.korniiienko.notesapp.ui.screens.MainViewModel
import com.korniiienko.notesapp.ui.screens.add.AddNoteViewModel
import com.korniiienko.notesapp.ui.screens.edit.EditNoteViewModel
import com.korniiienko.notesapp.ui.screens.notes.NotesViewModel

object ViewModelProvider {
    val Factory = viewModelFactory {

        initializer {
            MainViewModel(
                themeRepository = notesApplication().container.themeRepository
            )
        }

        initializer {
            NotesViewModel(
                remoteRepository = notesApplication().container.remoteRepository,
                localRepository = notesApplication().container.localRepository
            )
        }

        initializer {
            AddNoteViewModel(
                remoteRepository = notesApplication().container.remoteRepository,
                localRepository = notesApplication().container.localRepository
            )
        }

        initializer {
            EditNoteViewModel(
                savedStateHandle = this.createSavedStateHandle(),
                remoteRepository = notesApplication().container.remoteRepository,
                localRepository = notesApplication().container.localRepository,
            )
        }
    }
}

fun CreationExtras.notesApplication(): NotesApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as NotesApplication)