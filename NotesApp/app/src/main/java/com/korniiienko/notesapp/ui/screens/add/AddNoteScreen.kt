package com.korniiienko.notesapp.ui.screens.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.korniiienko.notesapp.R
import com.korniiienko.notesapp.di.ViewModelProvider
import com.korniiienko.notesapp.navigation.Screen
import com.korniiienko.notesapp.ui.shared.AddNoteComponent
import com.korniiienko.notesapp.ui.shared.TopAppBar
import com.korniiienko.notesapp.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddNoteViewModel = viewModel(factory = ViewModelProvider.Factory),
) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                name = Screen.AddNote.name,
                canNavigateBack = true,
                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
                navigateUp = navigateBack
            )
        }
    ) { paddingValue ->

        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .padding(
                    top = paddingValue.calculateTopPadding().plus(Spacing.medium),
                    start = Spacing.medium,
                    end = Spacing.medium,
                    bottom = paddingValue.calculateBottomPadding()
                ),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium)
        ) {
            AddNoteComponent(
                noteEntity = viewModel.entryUiState.currentNote,
                onValueChange = { viewModel.processIntent(AddNoteIntent.UpdateNote(it)) },
                modifier = modifier
            )

            Button(
                onClick = {
                    viewModel.processIntent(AddNoteIntent.SaveNote)
                    navigateBack()
                },
                enabled = viewModel.entryUiState.isEntryValid,
                modifier = modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.create))
            }
        }
    }
}