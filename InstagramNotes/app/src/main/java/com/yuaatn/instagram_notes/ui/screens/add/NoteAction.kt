package com.yuaatn.instagram_notes.ui.screens.add

sealed interface NoteAction {
    data class ModifyNote(val note: NoteEntity) : NoteAction
    data object CommitNote : NoteAction
}
