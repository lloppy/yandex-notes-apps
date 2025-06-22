package com.korniiienko.domain

import com.korniiienko.model.AppTheme

interface ThemeRepository {
    suspend fun getTheme(): AppTheme
    suspend fun setTheme(theme: AppTheme)
}
