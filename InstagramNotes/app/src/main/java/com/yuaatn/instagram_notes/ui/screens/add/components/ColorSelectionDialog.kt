package com.yuaatn.instagram_notes.ui.screens.add.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yuaatn.instagram_notes.R

@Composable
fun ColorSelectionDialog(
    startingColor: Color,
    onColorChosen: (Color) -> Unit,
    onClose: () -> Unit,
) {
    var colorState by remember {
        mutableStateOf(ColorState(startingColor, 1f))
    }

    AlertDialog(
        onDismissRequest = onClose,
        confirmButton = {
            Button(onClick = {
                onColorChosen(colorState.toFinal())
                onClose()
            }) {
                Text(text = stringResource(R.string.confirm))
            }
        },
        text = {
            ColorPickerBody(
                colorState = colorState,
                onColorChange = { colorState = it },
                modifier = Modifier.padding(16.dp)
            )
        }
    )
}

@Composable
private fun ColorPickerBody(
    colorState: ColorState,
    onColorChange: (ColorState) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ColorPreviewWithBrightness(
            color = colorState.color,
            brightness = colorState.brightness,
            onBrightnessChange = { onColorChange(colorState.copy(brightness = it)) }
        )
        HueGradientSelector(
            selectedColor = colorState.color,
            onColorSelected = { onColorChange(colorState.copy(color = it)) }
        )
    }
}

@Composable
private fun ColorPreviewWithBrightness(
    color: Color,
    brightness: Float,
    onBrightnessChange: (Float) -> Unit,
) {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(color.toArgb(), hsv)
    hsv[2] = brightness.coerceIn(0f, 1f)

    val adjustedColor = Color(android.graphics.Color.HSVToColor(hsv))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(adjustedColor)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Slider(
            value = brightness,
            onValueChange = onBrightnessChange,
            valueRange = 0.2f..1f,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun HueGradientSelector(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
) {
    var selectorX by remember { mutableFloatStateOf(0f) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        selectorX = offset.x.coerceIn(0f, size.width.toFloat())
                        val hue = (selectorX / size.width) * 360f
                        onColorSelected(Color.hsv(hue, 1f, 1f))
                    },
                    onDrag = { change, _ ->
                        selectorX = change.position.x.coerceIn(0f, size.width.toFloat())
                        val hue = (selectorX / size.width) * 360f
                        onColorSelected(Color.hsv(hue, 1f, 1f))
                    }
                )
            }
    ) {
        val widthPx = constraints.maxWidth.toFloat()

        LaunchedEffect(selectedColor) {
            val hsv = FloatArray(3)
            android.graphics.Color.colorToHSV(selectedColor.toArgb(), hsv)
            selectorX = (hsv[0] / 360f) * widthPx
        }

        GradientCanvas()

        Canvas(modifier = Modifier.fillMaxSize()) {
            val selectorRadius = 12.dp.toPx()
            val centerY = size.height / 2f
            val centerX = selectorX

            val crossSize = selectorRadius * 0.6f
            drawLine(
                color = Color.White,
                start = Offset(centerX - crossSize, centerY),
                end = Offset(centerX + crossSize, centerY),
                strokeWidth = 3f
            )
            drawLine(
                color = Color.White,
                start = Offset(centerX, centerY - crossSize),
                end = Offset(centerX, centerY + crossSize),
                strokeWidth = 3f
            )
        }
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

private data class ColorState(
    val color: Color,
    val brightness: Float,
) {
    fun toFinal(): Color {
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(color.toArgb(), hsv)
        hsv[2] = brightness.coerceIn(0f, 1f)
        return Color(android.graphics.Color.HSVToColor(hsv))
    }
}
