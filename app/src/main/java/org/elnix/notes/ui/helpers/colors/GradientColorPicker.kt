package org.elnix.notes.ui.helpers.colors

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import org.elnix.notes.R
import org.elnix.notes.ui.helpers.SliderWithLabel
import android.graphics.Color as AndroidColor


@Composable
fun GradientColorPicker(
    initialColor: Color,
//    defaultColor: Color,
//    onColorSelected: (Color) -> Unit
) {
    val hsvArray = remember {
        FloatArray(3).apply {
            AndroidColor.colorToHSV(initialColor.toArgb(), this)
        }
    }

    var hue by remember { mutableFloatStateOf(hsvArray[0]) }
    var sat by remember { mutableFloatStateOf(hsvArray[1]) }
    var value by remember { mutableFloatStateOf(hsvArray[2]) }

    var selectedColor by remember { mutableStateOf(initialColor) }
    var alpha by remember { mutableFloatStateOf(initialColor.alpha) }
    var hexText by remember { mutableStateOf(toHexWithAlpha(initialColor)) }

    val hueColor = remember(hue) { Color.hsv(hue, 1f, 1f) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        // --- Preview box ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(selectedColor.copy(alpha = alpha))
                .border(1.dp, MaterialTheme.colorScheme.outline),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = hexText,
                color = if (selectedColor.luminance() > 0.4) Color.Black else Color.White
            )
        }

        // --- Gradient + Hue selectors ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var pickerSize by remember { mutableFloatStateOf(0f) }

            // === Color Gradient Square ===
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Brush.horizontalGradient(listOf(Color.White, hueColor)))
                    .drawWithContent {
                        drawContent()
                        drawRect(
                            brush = Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black)
                            ),
                            blendMode = BlendMode.Multiply
                        )
                        // Draw selector circle
                        val x = sat * size.width
                        val y = (1f - value) * size.height
                        drawCircle(
                            color = if (selectedColor.luminance() > 0.5) Color.Black else Color.White,
                            radius = 10.dp.toPx(),
                            center = Offset(x, y),
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                    .pointerInput(hueColor) {
                        detectDragGestures(
                            onDragStart = { pos ->
                                pickerSize = size.width.toFloat()
                                sat = (pos.x / pickerSize).coerceIn(0f, 1f)
                                value = 1f - (pos.y / pickerSize).coerceIn(0f, 1f)
                                selectedColor = Color.hsv(hue, sat, value)
                                hexText = toHexWithAlpha(selectedColor.copy(alpha = alpha))
                            },
                            onDrag = { change, _ ->
                                pickerSize = size.width.toFloat()
                                sat = (change.position.x / pickerSize).coerceIn(0f, 1f)
                                value = 1f - (change.position.y / pickerSize).coerceIn(0f, 1f)
                                selectedColor = Color.hsv(hue, sat, value)
                                hexText = toHexWithAlpha(selectedColor.copy(alpha = alpha))
                            }
                        )
                    }
            )

            // === Hue Bar ===
            Box(
                modifier = Modifier
                    .width(25.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = (360 downTo 0 step 30).map { Color.hsv(it.toFloat(), 1f, 1f) }
                        )
                    )
                    .drawWithContent {
                        drawContent()
                        val y = (1 - hue / 360f) * size.height
                        drawLine(
                            color = Color.White,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 3.dp.toPx()
                        )
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { pos ->
                                hue = (1 - pos.y / size.height).coerceIn(0f, 1f) * 360f
                                selectedColor = Color.hsv(hue, sat, value)
                                hexText = toHexWithAlpha(selectedColor.copy(alpha = alpha))
                            },
                            onDrag = { change, _ ->
                                hue = (1 - change.position.y / size.height).coerceIn(0f, 1f) * 360f
                                selectedColor = Color.hsv(hue, sat, value)
                                hexText = toHexWithAlpha(selectedColor.copy(alpha = alpha))
                            }
                        )
                    }
            )
        }


        SliderWithLabel(
            label = stringResource(R.string.transparency),
            showValue = false,
            value = alpha,
            color = MaterialTheme.colorScheme.primary
        ) {
            alpha = it
            hexText = toHexWithAlpha(selectedColor.copy(alpha = alpha))
        }

        // --- HEX entry ---
        OutlinedTextField(
            value = hexText,
            onValueChange = {
                hexText = it
                runCatching {
                    if (it.startsWith("#")) {
                        selectedColor = Color(it.toColorInt())
                        alpha = parseAlpha(it)
                    }
                }
            },
            label = { Text("HEX") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

//        ValidateCancelButtons(
//            validateText = stringResource(R.string.apply),
//            cancelText = stringResource(R.string.reset),
//            onValidate = { onColorSelected(selectedColor.copy(alpha = alpha)) },
//            onCancel = {
//                selectedColor = defaultColor
//                alpha = defaultColor.alpha
//                hue = hsvArray[0]
//                sat = hsvArray[1]
//                value = hsvArray[2]
//                hexText = toHexWithAlpha(defaultColor)
//            }
//        )
    }
}



// --- Utility: extract alpha from hex like #RRGGBBAA ---
private fun parseAlpha(hex: String): Float {
    return try {
        if (hex.length == 9) {
            val alphaHex = hex.takeLast(2)
            alphaHex.toInt(16) / 255f
        } else 1f
    } catch (_: Exception) {
        1f
    }
}
