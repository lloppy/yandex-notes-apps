package com.yuaatn.instagram_notes.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yuaatn.instagram_notes.data.sync.NotesSynchronizer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: NotesSynchronizer
) : ViewModel() {

    fun processAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.InitializeSettings -> fetchNotes()
        }
    }

    private fun fetchNotes() {
        viewModelScope.launch {
            repository.synchronize()
        }
    }
}
