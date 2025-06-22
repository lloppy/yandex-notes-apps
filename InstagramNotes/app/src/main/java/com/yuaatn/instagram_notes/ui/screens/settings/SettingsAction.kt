package com.yuaatn.instagram_notes.ui.screens.settings

sealed interface SettingsAction {
    data object InitializeSettings : SettingsAction
}
