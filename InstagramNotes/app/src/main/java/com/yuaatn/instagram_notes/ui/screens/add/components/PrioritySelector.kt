package com.yuaatn.instagram_notes.ui.screens.add.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yuaatn.instagram_notes.model.Importance
import com.yuaatn.instagram_notes.ui.screens.add.NoteEntity

@Composable
fun PrioritySelector(
    entry: NoteEntity,
    onPriorityChange: (NoteEntity) -> Unit,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        modifier = Modifier.padding(vertical = 7.dp)
    ) {
        items(Importance.entries) { priority ->
            FilterChip(
                selected = entry.importance == priority,
                onClick = {
                    onPriorityChange(entry.copy(importance = priority))
                },
                label = { Text(text = priority.getUiName()) }
            )
        }
    }
}
