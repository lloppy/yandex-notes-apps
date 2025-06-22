package com.korniiienko.notesapp.ui.screens.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
fun EditNoteScreen(
    noteUid: String?,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditNoteViewModel = viewModel(factory = ViewModelProvider.Factory),
) {
    val state by viewModel.state.collectAsState()
    val openDialog = remember { mutableStateOf(false) }

    LaunchedEffect(noteUid) {
        noteUid?.let {
            viewModel.processIntent(EditNoteIntent.LoadNote(it))
        }
    }

    LaunchedEffect(state.isDeleted) {
        if (state.isDeleted) {
            navigateBack()
            viewModel.processIntent(EditNoteIntent.ResetDeleteState)
        }
    }

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = { openDialog.value = false },
            title = { Text(stringResource(R.string.delete_note)) },
            text = { Text(stringResource(R.string.sure_delete)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.processIntent(EditNoteIntent.DeleteNote)
                    },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.onErrorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.confirm))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                name = Screen.EditNote.name,
                canNavigateBack = true,
                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
                navigateUp = navigateBack
            )
        }
    ) { paddingValue ->
        Column(
            modifier = modifier
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = Spacing.medium
                )
                .padding(
                    top = paddingValue.calculateTopPadding()
                        .plus(Spacing.medium),
                    bottom = paddingValue.calculateBottomPadding()
                        .plus(Spacing.medium)
                ),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium)
        ) {
            AddNoteComponent(
                noteEntity = state.currentNote,
                onValueChange = { viewModel.processIntent(EditNoteIntent.UpdateNoteEntity(it)) },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.small)
            ) {
                OutlinedButton(
                    onClick = { openDialog.value = true },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.onErrorContainer),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(R.string.delete))
                }

                Button(
                    onClick = {
                        viewModel.processIntent(EditNoteIntent.UpdateNote)
                        navigateBack()
                    },
                    enabled = state.isEntryValid,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(R.string.save))
                }
            }
        }
    }
}