package com.yuaatn.instagram_notes.ui.screens.add.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yuaatn.instagram_notes.R

@Composable
fun HueSelector(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    onExpandPalette: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var currentColor by remember { mutableStateOf(selectedColor) }
    val defaultColors = listOf(
        Color(0xFFE53935),
        Color(0xFFFB8C00),
        Color(0xFFFFF176),
        Color(0xFF43A047),
        Color(0xFF29B6F6),
        Color(0xFF3949AB),
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.select_color),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            defaultColors.forEach { color ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(34.dp)
                        .background(color)
                        .border(
                            width = if (color == currentColor) 2.dp else 0.dp,
                            color = if (color == currentColor) Color.Black else Color.Transparent
                        )
                        .clickable {
                            currentColor = color
                            onColorSelected(color)
                        }
                ) {
                    if (color == currentColor) {
                        Text(
                            text = "✓",
                            color = Color.Black,
                            modifier = Modifier.padding(2.dp)
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color.Red,
                                Color.Yellow,
                                Color.Green,
                                Color.Cyan,
                                Color.Blue,
                                Color.Magenta,
                                Color.Red
                            )
                        )
                    )
                    .clickable { onExpandPalette() }
            )
        }
    }
}
