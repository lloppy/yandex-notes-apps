package com.korniiienko.notesapp.ui.shared.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.korniiienko.model.Importance
import com.korniiienko.notesapp.ui.screens.NoteEntity

@Composable
fun ImportanceComponent(
    noteEntity: NoteEntity,
    onValueChange: (NoteEntity) -> Unit,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        items(items = Importance.entries) { level ->
            FilterChip(
                selected = noteEntity.importance == level,
                onClick = {
                    onValueChange(
                        noteEntity.copy(importance = level)
                    )
                },
                label = { Text(text = level.toString()) }
            )
        }
    }
}