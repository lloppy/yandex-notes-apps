package com.korniiienko.notesapp.ui.screens.notes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.korniiienko.model.AppTheme
import com.korniiienko.model.Note
import com.korniiienko.notesapp.R
import com.korniiienko.notesapp.di.ViewModelProvider
import com.korniiienko.notesapp.navigation.Screen
import com.korniiienko.notesapp.ui.shared.LoadingCircle
import com.korniiienko.notesapp.ui.shared.SwipeCard
import com.korniiienko.notesapp.ui.shared.TopAppBar
import com.korniiienko.notesapp.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    appTheme: AppTheme,
    onClickAddNote: () -> Unit,
    onClickOpenNote: (String) -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
    viewModel: NotesViewModel = viewModel(factory = ViewModelProvider.Factory),
) {
    val state by viewModel.uiState.collectAsState()
    val openDialog = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.processIntent(NotesIntent.LoadNotes)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                name = Screen.MainNotes.name,
                appTheme = appTheme,
                canNavigateBack = false,
                scrollBehavior = scrollBehavior,
                onSync = {
                    viewModel.processIntent(NotesIntent.SyncFromServer)
                },
                onDeleteAllFromServer = {
                    viewModel.processIntent(NotesIntent.DeleteAllFromServer)
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onClickAddNote,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(Spacing.large)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = Icons.Default.Add.name
                )
            }
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValue ->

        when (val currentState = state) {
            is NotesState.Loading -> {
                LoadingCircle()
            }

            is NotesState.Success -> {
                if (openDialog.value) {
                    AlertDialog(
                        onDismissRequest = { openDialog.value = false },
                        title = { Text(stringResource(R.string.delete_note)) },
                        text = { Text(stringResource(R.string.sure_delete)) },
                        confirmButton = {
                            Button(
                                onClick = {
                                    currentState.currentNoteUid?.let {
                                        viewModel.processIntent(NotesIntent.DeleteNote(it))
                                        openDialog.value = false
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.onErrorContainer),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = stringResource(R.string.confirm))
                            }
                        }
                    )
                }

                NotesContent(
                    notes = currentState.notes,
                    onClickNote = { onClickOpenNote(it) },
                    onSwipeDelete = {
                        currentState.currentNoteUid = it
                        openDialog.value = true
                    },
                    onSwipeEdit = { onClickOpenNote(it) },
                    onClickGetNotesFromServer = {
                        viewModel.processIntent(NotesIntent.GetFromServer)
                    },
                    modifier = modifier,
                    contentPadding = paddingValue
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotesContent(
    notes: List<Note>,
    onClickNote: (String) -> Unit,
    onSwipeDelete: (String) -> Unit,
    onSwipeEdit: (String) -> Unit,
    onClickGetNotesFromServer: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
) {
    if (notes.isEmpty()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(Spacing.medium),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.no_items),
                textAlign = TextAlign.Center,
                modifier = modifier.fillMaxWidth(),
            )

            Button(onClick = onClickGetNotesFromServer) {
                Text(
                    text = stringResource(R.string.get_items),
                    textAlign = TextAlign.Center,
                    modifier = modifier.fillMaxWidth(),
                )
            }
        }

    } else {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(160.dp),
            verticalItemSpacing = 4.dp,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = modifier.fillMaxSize(),
            contentPadding = contentPadding,
        ) {
            items(items = notes, key = { it.uid }) { note ->
                SwipeCard(
                    note = note,
                    onActionDelete = { onSwipeDelete(note.uid) },
                    onActionEdit = { onSwipeEdit(note.uid) },
                    onClickNote = { onClickNote(note.uid) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.small)
                )
            }
        }
    }
}

