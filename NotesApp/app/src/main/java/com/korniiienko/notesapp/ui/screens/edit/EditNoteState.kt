package com.korniiienko.notesapp.ui.screens.edit

import com.korniiienko.notesapp.ui.screens.NoteEntity

data class EditNoteState(
    val currentNote: NoteEntity = NoteEntity(),
    val isEntryValid: Boolean = false,
    val isLoading: Boolean = false,
    val isDeleted: Boolean = false
)
