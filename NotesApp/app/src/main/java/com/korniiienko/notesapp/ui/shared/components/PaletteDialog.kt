package com.korniiienko.notesapp.ui.shared.components

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import com.korniiienko.notesapp.R
import com.korniiienko.notesapp.ui.theme.Spacing

@Composable
fun PaletteDialog(
    initialColor: Color,
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit,
) {
    var colorState by remember {
        mutableStateOf(ColorState(initialColor, initialColor.alpha))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            ConfirmButton(
                onConfirm = {
                    onColorSelected(colorState.toFinalColor())
                    onDismiss()
                }
            )
        },
        text = {
            ColorPickerContent(
                colorState = colorState,
                onColorChange = { colorState = it },
                modifier = Modifier.padding(Spacing.medium)
            )
        }
    )
}

@Composable
private fun ColorPickerContent(
    colorState: ColorState,
    onColorChange: (ColorState) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.medium)
    ) {
        ColorPreviewWithSlider(
            color = colorState.color,
            brightness = colorState.brightness,
            onBrightnessChange = { onColorChange(colorState.copy(brightness = it)) }
        )

        ColorGradientPicker(
            onColorSelected = { onColorChange(colorState.copy(color = it)) }
        )
    }
}

@Composable
private fun ColorPreviewWithSlider(
    color: Color,
    brightness: Float,
    onBrightnessChange: (Float) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ColorPreviewBox(color.copy(alpha = brightness))
        Spacer(modifier = Modifier.width(Spacing.small))
        BrightnessSlider(
            brightness = brightness,
            onBrightnessChange = onBrightnessChange
        )
    }
}

@Composable
private fun ColorPreviewBox(color: Color) {
    Box(
        modifier = Modifier
            .size(Spacing.dot)
            .clip(RoundedCornerShape(Spacing.corners))
            .background(color)
    )
}

@Composable
private fun BrightnessSlider(
    brightness: Float,
    onBrightnessChange: (Float) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stringResource(R.string.brightness),
            modifier = Modifier.padding(end = Spacing.small)
        )
        Slider(
            value = brightness,
            onValueChange = onBrightnessChange,
            valueRange = 0.2f..1f,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ColorGradientPicker(onColorSelected: (Color) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(Spacing.colorPicker)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val x = offset.x.coerceIn(0f, size.width.toFloat())
                    val hue = (x / size.width) * 360
                    onColorSelected(Color.hsv(hue, 1f, 1f))
                }
            }
    ) {
        GradientCanvas()
    }
}

@Composable
private fun GradientCanvas() {
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

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.horizontalGradient(
                colors = gradientColors,
                tileMode = TileMode.Clamp
            )
        )
    }
}

@Composable
private fun ConfirmButton(onConfirm: () -> Unit) {
    Button(onClick = onConfirm) {
        Text(text = stringResource(R.string.confirm))
    }
}

private data class ColorState(
    val color: Color,
    val brightness: Float,
) {
    fun toFinalColor() = color.copy(alpha = brightness).also {
        Log.d("ColorPicker", "Final color in color picker is: ${color.copy(alpha = brightness)}")
    }

}
