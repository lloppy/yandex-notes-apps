package com.yuaatn.instagram_notes.ui.screens.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yuaatn.instagram_notes.R
import com.yuaatn.instagram_notes.ui.screens.add.components.EntryFormComponent

@Composable
fun NoteEditScreen(
    noteId: String?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NoteEditViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val showDeleteDialog = remember { mutableStateOf(false) }

    LaunchedEffect(noteId) {
        noteId?.let { viewModel.processAction(NoteEditAction.FetchNote(it)) }
    }

    LaunchedEffect(uiState.noteDeleted) {
        if (uiState.noteDeleted) {
            onNavigateBack()
            viewModel.processAction(NoteEditAction.ClearDeletionFlag)
        }
    }

    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = { Text(stringResource(R.string.delete_note)) },
            text = { Text(stringResource(R.string.sure_delete)) },
            confirmButton = {
                Button(
                    onClick = { viewModel.processAction(NoteEditAction.RemoveNote) },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.confirm))
                }
            }
        )
    }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = 12.dp,
                vertical = 8.dp
            ),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        EntryFormComponent(
            entry = uiState.currentNote,
            onEntryChange = { viewModel.processAction(NoteEditAction.ModifyNoteEntity(it)) },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                viewModel.processAction(NoteEditAction.SaveChanges)
                onNavigateBack()
            },
            enabled = uiState.isValidEntry,
            modifier = modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.save))
        }

        Button(
            onClick = { showDeleteDialog.value = true },
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error),
            modifier = modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.delete))
        }
    }
}
