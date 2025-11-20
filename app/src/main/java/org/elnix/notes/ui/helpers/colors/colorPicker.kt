package org.elnix.notes.ui.helpers.colors

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.notes.R
import org.elnix.notes.data.helpers.ColorPickerMode
import org.elnix.notes.data.helpers.colorPickerText
import org.elnix.notes.data.settings.stores.ColorModesSettingsStore.getColorPickerMode
import org.elnix.notes.data.settings.stores.ColorModesSettingsStore.setColorPickerMode
import org.elnix.notes.ui.helpers.randomColor
import org.elnix.notes.ui.theme.AppObjectsColors
import org.elnix.notes.ui.theme.adjustBrightness

@Composable
fun ColorPickerRow(
    label: String,
    showLabel: Boolean = true,
    enabled: Boolean = true,
    defaultColor: Color,
    currentColor: Color,
    randomColorButton: Boolean = true,
    resetButton: Boolean = true,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    onColorPicked: (Color) -> Unit
) {
    var showPicker by remember { mutableStateOf(false) }
    var actualColor by remember { mutableStateOf(currentColor) }

    val modifier = if (showLabel) Modifier.fillMaxWidth() else Modifier.wrapContentWidth()
    Row(
       modifier = modifier
           .clickable(enabled) { showPicker = true }
           .background(
               color = backgroundColor.copy(if (enabled) 1f else 0.5f),
               shape = RoundedCornerShape(12.dp)
           )
           .padding(horizontal = 16.dp, vertical = 14.dp),

        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if(showLabel){
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurface.copy(if (enabled) 1f else 0.5f),
                modifier = Modifier.weight(1f),
                maxLines = Int.MAX_VALUE,
                softWrap = true
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            if (randomColorButton) {
                Icon(
                    imageVector = Icons.Default.Shuffle,
                    contentDescription = stringResource(R.string.random_color),
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(backgroundColor.adjustBrightness(0.8f))
                        .padding(5.dp)
                        .clickable { onColorPicked(randomColor()) }
                )
            }

            Spacer(Modifier.width(8.dp))

            if (resetButton) {
                Icon(
                    imageVector = Icons.Default.Restore,
                    contentDescription = stringResource(R.string.reset),
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(backgroundColor.adjustBrightness(0.8f))
                        .padding(5.dp)
                        .clickable { onColorPicked(defaultColor) }
                )
            }

            Spacer(Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(currentColor, shape = CircleShape)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outline,
                        CircleShape
                    )
            )
        }
    }

    if (showPicker) {
        AlertDialog(
            onDismissRequest = { showPicker = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){

                    Icon(
                        imageVector = Icons.Default.Restore,
                        contentDescription = stringResource(R.string.reset),
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier
                            .clip(CircleShape)
                            .padding(8.dp)
                            .clickable { actualColor = defaultColor }
                    )

                    Spacer(Modifier.width(15.dp))

                    Text(
                        text = "${stringResource(R.string.pick_a)} $label ${stringResource(R.string.color_text_literal)}",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            text = {
                ColorPicker(
                    initialColor = actualColor,
//                    defaultColor = defaultColor,
//                    onColorSelected = { actualColor = it }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onColorPicked(actualColor)
                        showPicker = false
                    },
                    colors = AppObjectsColors.buttonColors()
                ) {
                    Text(
                        text = stringResource(R.string.save),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showPicker = false },
                    colors = AppObjectsColors.cancelButtonColors()
                ) {
                    Text(stringResource(R.string.close))
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}



@Composable
private fun ColorPicker(
    initialColor: Color,
//    defaultColor: Color,
//    onColorSelected: (Color) -> Unit
) {

    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val mode by getColorPickerMode(ctx).collectAsState(initial = ColorPickerMode.SLIDERS)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .clip(RoundedCornerShape(8.dp)),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ColorPickerMode.entries.forEach{ actualMode ->
                Text(
                    text = colorPickerText(actualMode),
                    color = if (mode == actualMode) MaterialTheme.colorScheme.onSecondary
                            else MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            scope.launch { setColorPickerMode(ctx, actualMode) }
                        }
                        .background(
                            if (mode == actualMode) MaterialTheme.colorScheme.secondary
                            else MaterialTheme.colorScheme.surface
                        )
                        .padding(12.dp)
                )
            }
        }


        when (mode) {
            ColorPickerMode.SLIDERS -> SliderColorPicker(
                actualColor = initialColor,
//                defaultColor = defaultColor,
//                onColorSelected = onColorSelected
            )

            ColorPickerMode.GRADIENT -> GradientColorPicker(
                initialColor = initialColor,
//                defaultColor = defaultColor,
//                onColorSelected = onColorSelected
            )

            ColorPickerMode.DEFAULTS -> DefaultColorPicker(
                initialColor = initialColor,
//                defaultColor = defaultColor,
//                onColorSelected = onColorSelected
            )
        }
    }
}




// --- Utility: convert color → #RRGGBBAA ---
fun toHexWithAlpha(color: Color): String {
    val argb = color.toArgb()
    val rgb = argb and 0xFFFFFF
    val alpha = (color.alpha * 255).toInt().coerceIn(0, 255)
    return "#${"%06X".format(rgb)}${"%02X".format(alpha)}"
}