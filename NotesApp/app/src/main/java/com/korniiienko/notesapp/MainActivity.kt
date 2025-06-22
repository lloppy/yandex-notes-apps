package com.korniiienko.notesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.compose.LocalThemeChange
import com.example.compose.NotesAppTheme
import com.korniiienko.model.AppTheme
import com.korniiienko.notesapp.di.ViewModelProvider
import com.korniiienko.notesapp.ui.NotesApp
import com.korniiienko.notesapp.ui.screens.MainViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {

            val mainViewModel: MainViewModel = viewModel(factory = ViewModelProvider.Factory)
            val themeState by mainViewModel.themeState.collectAsState()

            CompositionLocalProvider(
                LocalThemeChange provides mainViewModel::setTheme
            ) {
                NotesAppTheme(darkTheme = themeState == AppTheme.DARK) {
                    NotesApp(
                        appTheme = themeState
                    )
                }
            }
        }
    }
}
