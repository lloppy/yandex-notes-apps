package com.korniiienko.notesapp.ui.screens.notes

sealed interface NotesIntent {
    object LoadNotes : NotesIntent
    object DeleteAllFromServer : NotesIntent
    object SyncFromServer : NotesIntent
    object GetFromServer : NotesIntent
    data class DeleteNote(val noteId: String) : NotesIntent
}