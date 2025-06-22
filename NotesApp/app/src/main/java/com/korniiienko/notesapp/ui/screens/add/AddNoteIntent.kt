package com.korniiienko.notesapp.ui.screens.add

import com.korniiienko.notesapp.ui.screens.NoteEntity

sealed interface AddNoteIntent {
    data class UpdateNote(val note: NoteEntity) : AddNoteIntent
    object SaveNote : AddNoteIntent
}