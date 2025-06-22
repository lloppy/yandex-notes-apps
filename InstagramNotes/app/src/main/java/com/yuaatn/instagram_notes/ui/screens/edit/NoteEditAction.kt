package com.yuaatn.instagram_notes.ui.screens.edit

import com.yuaatn.instagram_notes.ui.screens.add.NoteEntity

sealed interface NoteEditAction {
    data class FetchNote(val noteId: String) : NoteEditAction
    data class ModifyNoteEntity(val note: NoteEntity) : NoteEditAction
    data object SaveChanges : NoteEditAction
    data object RemoveNote : NoteEditAction
    data object ClearDeletionFlag : NoteEditAction
}