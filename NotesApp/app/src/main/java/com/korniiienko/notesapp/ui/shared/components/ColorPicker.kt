package com.korniiienko.notesapp.ui.shared.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import com.korniiienko.notesapp.R
import com.korniiienko.notesapp.ui.theme.Spacing

@Composable
fun ColorPicker(
    currentColor: Color,
    onColorSelected: (Color) -> Unit,
    onOpenFullPalette: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var state by remember(currentColor) {
        mutableStateOf(
            ColorPickerState(
                selectedColor = currentColor,
                isCustom = !currentColor.isDefault()
            )
        )
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.small),
        modifier = modifier.fillMaxWidth()
    ) {
        PickerHeader()
        ColorGrid(
            state = state,
            onColorSelect = { color ->
                state = state.copy(selectedColor = color)
                onColorSelected(color)
            },
            onOpenFullPalette = onOpenFullPalette
        )
    }
}

@Composable
private fun PickerHeader() {
    Text(
        text = stringResource(R.string.select_color),
        modifier = Modifier.padding(bottom = Spacing.small)
    )
}

@Composable
private fun ColorGrid(
    state: ColorPickerState,
    onColorSelect: (Color) -> Unit,
    onOpenFullPalette: () -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
        DefaultColors.forEach { color ->
            ColorChip(
                color = color,
                isSelected = color == state.selectedColor,
                onSelect = onColorSelect
            )
        }
        PaletteExpander(
            currentColor = state.selectedColor,
            onExpandClick = onOpenFullPalette,
        )
    }
}

@Composable
private fun ColorChip(
    color: Color,
    isSelected: Boolean,
    onSelect: (Color) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(Spacing.colorBox)
            .background(color = color)
            .border(
                width = if (isSelected) Spacing.indicator else Spacing.noPadding,
                color = if (isSelected) Color.Black else Color.Transparent
            )
            .clickableColorChip { onSelect(color) }
    ) {
        SelectionIndicator(visible = isSelected)
    }
}

@Composable
private fun PaletteExpander(
    currentColor: Color,
    onExpandClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(Spacing.colorBox)
            .background(
                if (currentColor.isDefault() || currentColor == Color.White) gradientBrush()
                else Brush.verticalGradient(
                    listOf(
                        currentColor,
                        currentColor.copy(alpha = 0.7f),
                        currentColor.copy(alpha = 0.4f)
                    )
                )
            )
            .border(
                width = if (!currentColor.isDefault()) Spacing.indicator else Spacing.noPadding,
                color = if (!currentColor.isDefault()) Color.Black else Color.Transparent
            )
            .clickableColorChip(
                onExpandClick
            )
    ) {
        SelectionIndicator(visible = !currentColor.isDefault())
    }
}

@Composable
private fun SelectionIndicator(visible: Boolean) {
    if (visible) {
        Text(
            text = "✓",
            modifier = Modifier.padding(start = Spacing.indicator)
        )
    }
}

private fun Modifier.clickableColorChip(onClick: () -> Unit) = pointerInput(Unit) {
    detectTapGestures { onClick() }
}

@Composable
private fun gradientBrush(): Brush {
    val gradientColors = remember {
        listOf(
            Color.Red,
            Color.Yellow,
            Color.Green,
            Color.Cyan,
            Color.Blue,
            Color.Magenta,
            Color.Red
        )
    }
    return Brush.horizontalGradient(gradientColors)
}

private fun Color.isDefault() = this in DefaultColors

private data class ColorPickerState(
    val selectedColor: Color,
    val isCustom: Boolean,
)

private val DefaultColors = listOf(
    Color(0xFFF44336),
    Color(0xFFFF9800),
    Color(0xFFFFEB3B),
    Color(0xFF4CAF50),
    Color(0xFF03A9F4),
    Color(0xFF3F51B5),
)
