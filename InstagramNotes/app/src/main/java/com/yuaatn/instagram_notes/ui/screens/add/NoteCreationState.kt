package com.yuaatn.instagram_notes.ui.screens.add

data class NoteCreationState(
    val currentNote: NoteEntity = NoteEntity(),
    val isValid: Boolean = false,
)
