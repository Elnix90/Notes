package org.elnix.notes.ui.helpers

import android.content.Context
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.elnix.notes.data.helpers.ColorPickerMode
import org.elnix.notes.data.settings.UiSettingsStore.getColorPickerMode
import org.elnix.notes.data.settings.UiSettingsStore.setColorPickerMode
import org.elnix.notes.ui.theme.adjustBrightness

@Composable
fun ColorPickerRow(label: String, defaultColor: Color, currentColor: Int, scope: CoroutineScope, onColorPicked: (Int) -> Unit) {
    var showPicker by remember { mutableStateOf(false) }

    Row(
        Modifier
            .fillMaxWidth()
            .clickable { showPicker = true }
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),

        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(currentColor), shape = CircleShape)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.onSurface.adjustBrightness(0.3f),
                        CircleShape
                    )
            )
        }
    }

    if (showPicker) {
        AlertDialog(
            onDismissRequest = { showPicker = false },
            title = { Text(text = "Pick a $label color", color = MaterialTheme.colorScheme.onSurface) },
            text = {
                ColorPicker(
                    scope,
                    initialColor = Color(currentColor),
                    defaultColor = defaultColor,
                    onColorSelected = {
                        onColorPicked(it.toArgb())
                        showPicker = false
                    }
                )
            },
            confirmButton = {},
            dismissButton = {},
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}



@Composable
private fun ColorPicker(
    scope: CoroutineScope,
    initialColor: Color,
    defaultColor: Color,
    onColorSelected: (Color) -> Unit
) {
    val ctx = LocalContext.current
    val mode by getColorPickerMode(ctx).collectAsState(initial = ColorPickerMode.SLIDERS)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable { changeSliderMode(ctx, scope, mode) },
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sliders",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium
            )
            Switch(
                checked = mode == ColorPickerMode.GRADIENT,
                onCheckedChange = { changeSliderMode(ctx, scope, mode) }
            )
            Text(
                text = "Gradient",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium
            )
        }


        when (mode) {
            ColorPickerMode.SLIDERS -> SliderColorPicker(
                initialColor = initialColor,
                defaultColor = defaultColor,
                onColorSelected = onColorSelected
            )

            ColorPickerMode.GRADIENT -> GradientColorPicker(
                initialColor = initialColor,
                defaultColor = defaultColor,
                onColorSelected = onColorSelected
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


private fun changeSliderMode(ctx: Context, scope: CoroutineScope, mode: ColorPickerMode) {
    scope.launch{
        setColorPickerMode(ctx, if (mode == ColorPickerMode.SLIDERS) ColorPickerMode.GRADIENT else ColorPickerMode.SLIDERS)
    }
}