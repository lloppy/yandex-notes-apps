package com.korniiienko.notesapp.ui.shared

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.compose.LocalThemeChange
import com.korniiienko.notesapp.R
import com.korniiienko.model.AppTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TopAppBar(
    name: String,
    appTheme: AppTheme? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit = { },
    onSync: () -> Unit = { },
    onDeleteAllFromServer: () -> Unit = { },
) {
    CenterAlignedTopAppBar(
        title = { Text(text = name, style = MaterialTheme.typography.headlineSmall) },
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = Icons.AutoMirrored.Filled.ArrowBack.name
                    )
                }
            } else {
                if (appTheme != null) {
                    val isDark = when (appTheme) {
                        AppTheme.DARK -> true
                        AppTheme.LIGHT -> false
                        AppTheme.SYSTEM -> isSystemInDarkTheme()
                    }
                    val onChangeTheme = LocalThemeChange.current

                    IconButton(
                        onClick = {
                            onChangeTheme?.invoke(
                                if (isDark) AppTheme.LIGHT else AppTheme.DARK
                            )
                        }
                    ) {
                        Icon(
                            painter = painterResource(
                                if (isDark) R.drawable.ic_dark_mode
                                else R.drawable.ic_light_mode
                            ),
                            contentDescription = stringResource(R.string.switch_theme)
                        )
                    }
                }
            }
        },
        actions = {
            if (!canNavigateBack) {
                IconButton(onClick = onSync) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = stringResource(R.string.sync)
                    )
                }
                IconButton(onClick = onDeleteAllFromServer) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = stringResource(R.string.delete)
                    )
                }
            }
        }
    )
}