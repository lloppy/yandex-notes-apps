package com.yuaatn.instagram_notes.ui.screens.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yuaatn.instagram_notes.R
import com.yuaatn.instagram_notes.ui.screens.add.components.EntryFormComponent

@Composable
fun NoteCreationScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NoteCreationViewModel = hiltViewModel(),
) {
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
            entry = viewModel.uiState.currentNote,
            onEntryChange = { viewModel.processAction(NoteAction.ModifyNote(it)) },
            modifier = modifier
        )

        Button(
            onClick = {
                viewModel.processAction(NoteAction.CommitNote)
                onBack()
            },
            enabled = viewModel.uiState.isValid,
            modifier = modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.create))
        }
    }
}
