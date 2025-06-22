package com.korniiienko.notesapp.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.korniiienko.domain.ThemeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val themeRepository: ThemeRepository,
) : ViewModel() {

    private val _themeState = MutableStateFlow(com.korniiienko.model.AppTheme.SYSTEM)
    val themeState: StateFlow<com.korniiienko.model.AppTheme> = _themeState.asStateFlow()

    init {
        viewModelScope.launch {
            val currentTheme = themeRepository.getTheme()
            _themeState.value = currentTheme
        }
    }

    fun setTheme(theme: com.korniiienko.model.AppTheme) {
        viewModelScope.launch {
            themeRepository.setTheme(theme)
            _themeState.value = theme
        }
    }
}