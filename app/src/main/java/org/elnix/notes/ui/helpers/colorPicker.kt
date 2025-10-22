package org.elnix.notes.ui.helpers

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import org.elnix.notes.ui.theme.AppObjectsColors
import org.elnix.notes.ui.theme.blendWith
import kotlin.random.Random

@Composable
fun ColorPickerRow(label: String, currentColor: Int, onColorPicked: (Int) -> Unit) {
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
            color = MaterialTheme.colorScheme.onBackground
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(currentColor), shape = CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f), CircleShape)
            )
        }
    }

    if (showPicker) {
        AlertDialog(
            onDismissRequest = { showPicker = false },
            title = { Text(text = "Pick a $label color", color = MaterialTheme.colorScheme.onBackground) },
            text = {
                ColorPicker(
                    initialColor = Color(currentColor),
                    onColorSelected = {
                        onColorPicked(it.toArgb())
                        showPicker = false
                    }
                )
            },
            confirmButton = {},
            dismissButton = {},
            containerColor = MaterialTheme.colorScheme.background.blendWith(MaterialTheme.colorScheme.primary, 0.3f)
        )
    }
}

@Composable
fun ColorPicker(initialColor: Color, onColorSelected: (Color) -> Unit) {
    var red by remember { mutableFloatStateOf(initialColor.red) }
    var green by remember { mutableFloatStateOf(initialColor.green) }
    var blue by remember { mutableFloatStateOf(initialColor.blue) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text ="Preview",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge
            )
            IconButton(onClick = {
                red = Random.nextFloat()
                green = Random.nextFloat()
                blue = Random.nextFloat()
            }) {
                Icon(Icons.Default.Shuffle, contentDescription = "Random Color")
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(Color(red, green, blue))
                .border(1.dp, MaterialTheme.colorScheme.outline)
        )

        SliderWithLabel("R", red) { red = it }
        SliderWithLabel("G", green) { green = it }
        SliderWithLabel("B", blue) { blue = it }

        Spacer(Modifier.height(12.dp))
        Button(
            onClick = { onColorSelected(Color(red, green, blue)) },
            modifier = Modifier.fillMaxWidth(),
            colors = AppObjectsColors.buttonColors()
        ) {
            Text("Apply")
        }
    }
}
