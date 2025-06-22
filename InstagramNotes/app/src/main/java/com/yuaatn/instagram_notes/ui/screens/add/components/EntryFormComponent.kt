package com.yuaatn.instagram_notes.ui.screens.add.components

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yuaatn.instagram_notes.R
import com.yuaatn.instagram_notes.ui.screens.add.NoteEntity
import com.yuaatn.instagram_notes.ui.screens.add.formattedDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Calendar

@Composable
fun EntryFormComponent(
    entry: NoteEntity,
    onEntryChange: (NoteEntity) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var isColorDialogVisible by remember { mutableStateOf(false) }
    var currentColor by remember { mutableStateOf(Color(entry.color)) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showDateButton by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        OutlinedTextField(
            value = entry.title,
            onValueChange = { onEntryChange(entry.copy(title = it)) },
            label = { Text(text = stringResource(R.string.set_name)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = entry.content,
            onValueChange = { onEntryChange(entry.copy(content = it)) },
            label = { Text(text = stringResource(R.string.set_content)) },
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 110.dp)
        )

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = entry.expirationDate != null,
                    onCheckedChange = { isChecked ->
                        if (isChecked) {
                            showDateButton = true
                        } else {
                            onEntryChange(entry.copy(expirationDate = null))
                            showDateButton = false
                        }
                    }
                )
                Text(text = stringResource(R.string.self_destruct))
            }
            if (showDateButton) {
                Button(onClick = {
                    showDatePicker = true
                }) {
                    Text(text = "Выбрать дату")
                }
                if (entry.expirationDate != null) {
                    Text(text = "Дата самоуничтожения: ${entry.expirationDate.formattedDate()}")
                }
            }
        }

        HueSelector(
            selectedColor = currentColor,
            onColorSelected = { newColor ->
                currentColor = newColor
                onEntryChange(entry.copy(color = newColor.toArgb()))
            },
            onExpandPalette = { isColorDialogVisible = true }
        )

        PrioritySelector(
            entry = entry,
            onPriorityChange = onEntryChange
        )

        if (isColorDialogVisible) {
            ColorSelectionDialog(
                startingColor = currentColor,
                onColorChosen = { newColor ->
                    currentColor = newColor
                    onEntryChange(entry.copy(color = newColor.toArgb()))
                    isColorDialogVisible = false
                },
                onClose = { isColorDialogVisible = false }
            )
        }

        if (showDatePicker) {
            val context = LocalContext.current
            val calendar = Calendar.getInstance()

            DatePickerDialog(
                context,
                { _, year, month, day ->
                    val localDateTime = LocalDateTime.of(
                        year,
                        month + 1,
                        day,
                        0,
                        0
                    )
                    val timestamp = localDateTime.toEpochSecond(ZoneOffset.UTC)
                    onEntryChange(entry.copy(expirationDate = timestamp))
                    showDatePicker = false
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }
}
