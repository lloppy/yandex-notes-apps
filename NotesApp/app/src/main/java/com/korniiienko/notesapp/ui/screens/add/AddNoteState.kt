package com.korniiienko.notesapp.ui.screens.add

import com.korniiienko.notesapp.ui.screens.NoteEntity

data class AddNoteState(
    val currentNote: NoteEntity = NoteEntity(),
    val isEntryValid: Boolean = false,
)
