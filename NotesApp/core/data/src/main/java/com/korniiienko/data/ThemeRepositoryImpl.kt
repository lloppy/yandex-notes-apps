package com.korniiienko.data

import android.content.Context
import com.korniiienko.domain.ThemeRepository
import com.korniiienko.model.AppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ThemeRepositoryImpl(
    context: Context,
) : ThemeRepository {

    private val sharedPreferences =
        context.getSharedPreferences(SHARED_PREF_THEME_KEY, Context.MODE_PRIVATE)

    override suspend fun getTheme(): AppTheme = withContext(Dispatchers.IO) {
        val themeName = sharedPreferences.getString(THEME_KEY, AppTheme.SYSTEM.name)
            ?: AppTheme.SYSTEM.name
        AppTheme.valueOf(themeName)
    }

    override suspend fun setTheme(theme: AppTheme) = withContext(Dispatchers.IO) {
        sharedPreferences.edit().putString(THEME_KEY, theme.name).apply()
    }

    companion object {
        private const val THEME_KEY = "theme"
        private const val SHARED_PREF_THEME_KEY = "theme_prefs"
    }
}
