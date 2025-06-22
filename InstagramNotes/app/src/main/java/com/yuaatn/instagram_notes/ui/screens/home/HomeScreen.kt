package com.yuaatn.instagram_notes.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yuaatn.instagram_notes.R
import com.yuaatn.instagram_notes.model.Note
import com.yuaatn.instagram_notes.ui.screens.home.components.InstaStyleNoteCard
import com.yuaatn.instagram_notes.ui.screens.home.components.InstagramFloatingButton
import com.yuaatn.instagram_notes.ui.screens.home.components.LoadingCircle
import com.yuaatn.instagram_notes.ui.screens.home.components.ProfileHeader

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onCreateNote: () -> Unit,
    onModifyNote: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when (val current = uiState) {
            is HomeState.Loading -> LoadingCircle()
            is HomeState.Success -> NotesGrid(
                notes = current.notes,
                onEdit = onModifyNote,
                onDeleteSwipe = { viewModel.processAction(HomeAction.RemoveNote(it)) },
                modifier = modifier
            )
        }

        InstagramFloatingButton(
            onClick = onCreateNote,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 40.dp, end = 16.dp)
        )
    }
}

@Composable
private fun NotesGrid(
    notes: List<Note>,
    onEdit: (String) -> Unit,
    onDeleteSwipe: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    ProfileHeader {
        if (notes.isEmpty()) {
            Text(
                text = stringResource(R.string.no_created_items),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
        } else {
            LazyVerticalGrid(
                modifier = Modifier.fillMaxWidth(),
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notes, key = { it.uid }) { note ->
                    InstaStyleNoteCard(
                        note = note,
                        onDelete = { onDeleteSwipe(note.uid) },
                        onClick = { onEdit(note.uid) },
                        modifier = Modifier
                            .aspectRatio(1f)
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}
