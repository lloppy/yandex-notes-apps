package com.yuaatn.instagram_notes.ui.screens.home

sealed interface HomeAction {
    data object InitializeHome : HomeAction
    data class RemoveNote(val noteUid: String) : HomeAction
}
