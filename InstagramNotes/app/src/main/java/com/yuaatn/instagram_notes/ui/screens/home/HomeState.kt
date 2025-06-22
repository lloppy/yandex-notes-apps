package com.yuaatn.instagram_notes.ui.screens.home

import com.yuaatn.instagram_notes.model.Note

sealed interface HomeState {
    data class Success(val notes: List<Note> = emptyList()) : HomeState
    data object Loading : HomeState
}
