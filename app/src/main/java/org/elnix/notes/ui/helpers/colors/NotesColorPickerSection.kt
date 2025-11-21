package org.elnix.notes.ui.helpers.colors

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.elnix.notes.R
import org.elnix.notes.data.NoteEntity
import org.elnix.notes.ui.theme.AppObjectsColors

@Composable
fun NotesColorPickerSection(
    note: NoteEntity,
    onBgColorPicked: (Color) -> Unit,
    onTextColorPicked: (Color) -> Unit,
    onAutoSwitchToggle: (Boolean) -> Unit
) {
    val autoTextColorEnabled = note.autoTextColor
    val luminance = remember { mutableFloatStateOf(0.8f) }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // NOTE BACKGROUND COLOR PICKER
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val label = stringResource(R.string.note_color)

                Text(
                    text = label,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.labelSmall
                )
                Spacer(Modifier.height(4.dp))

                ColorPickerRow(
                    label = label,
                    showLabel = false,
                    defaultColor = MaterialTheme.colorScheme.surface,
                    currentColor = note.bgColor
                        ?: MaterialTheme.colorScheme.surface,
                    backgroundColor = MaterialTheme.colorScheme.primary,
                    maxLuminance = luminance.floatValue,
                    randomColorButton = true
                ) { onBgColorPicked(it) }

                Spacer(Modifier.height(4.dp))

                LuminanceSlider(
                    value = luminance.floatValue,
                    onValueChange = { luminance.floatValue = it },
                    modifier = Modifier.padding(8.dp)
                )
            }

            // TEXT COLOR PICKER
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val label = stringResource(R.string.text_color)
                Text(
                    text = label,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.labelSmall
                )

                Spacer(Modifier.height(4.dp))

                ColorPickerRow(
                    label = label,
                    showLabel = false,
                    defaultColor = MaterialTheme.colorScheme.onSurface,
                    currentColor = note.txtColor
                        ?: MaterialTheme.colorScheme.onSurface,
                    enabled = !autoTextColorEnabled,
                    backgroundColor = MaterialTheme.colorScheme.primary,
                    randomColorButton = false
                ) { onTextColorPicked(it) }

                Spacer(Modifier.height(4.dp))

                // AUTO TEXT COLOR CHECKBOX
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = autoTextColorEnabled,
                        onCheckedChange = { onAutoSwitchToggle(it) },
                        colors = AppObjectsColors.checkboxColors()
                    )
                    Text(
                        text = stringResource(R.string.auto_text_color),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(end = 10.dp)
                    )
                }
            }
        }
    }
}



@Composable
private fun LuminanceSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    // Container width in pixels (will be updated later)
    val sliderWidth = 150.dp

    // Remember the current slider position on the gradient (in 0..sliderWidth)
    val thumbRadius = 9.dp
    val displayWidthPx = sliderWidth.value * 3 // approximate px for offset calculation, adjust as needed

    Box(
        modifier = modifier
            .height(20.dp)
            .width(sliderWidth)
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color.Black, Color.White),
                    start = Offset.Zero,
                    end = Offset(displayWidthPx, 0f)
                )
            )
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    val newX = change.position.x.coerceIn(0f, size.width.toFloat())
                    val newValue = newX / size.width
                    onValueChange(newValue)
                }
            }
    ) {
        Canvas(
            modifier = Modifier
                .height(20.dp)
                .width(sliderWidth)
        ) {
            // Ensure the thumb is fully inside the slider bounds
            val min = thumbRadius.toPx() + 1
            val max = size.width - thumbRadius.toPx() -1
            val x = (min + (max - min) * value).coerceIn(min, max)

            // Calculate color based on luminance value (slider progress)
            val thumbColor = if (value < 0.5f) Color.White else Color.Black


            drawCircle(
                color = thumbColor,
                radius = thumbRadius.toPx(),
                center = Offset(x, size.height / 2),
                style = Stroke(width = 2.dp.toPx())
            )
        }
    }
}
