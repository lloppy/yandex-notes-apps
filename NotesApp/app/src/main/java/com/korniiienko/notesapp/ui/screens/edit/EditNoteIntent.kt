package com.korniiienko.notesapp.ui.screens.edit

import com.korniiienko.notesapp.ui.screens.NoteEntity

sealed interface EditNoteIntent {
    data class LoadNote(val uid: String) : EditNoteIntent
    data class UpdateNoteEntity(val note: NoteEntity) : EditNoteIntent
    object UpdateNote : EditNoteIntent
    object DeleteNote : EditNoteIntent
    object ResetDeleteState : EditNoteIntent
}