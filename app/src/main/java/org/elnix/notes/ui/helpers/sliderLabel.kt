package org.elnix.notes.ui.helpers

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.elnix.notes.ui.theme.AppObjectsColors

@Composable
fun SliderWithLabel(label: String, value: Float, onChange: (Float) -> Unit) {
    Column {
        Text(
            text = "$label: ${(value * 255).toInt()}",
            color = MaterialTheme.colorScheme.onSurface
        )
        Slider(
            value = value,
            onValueChange = onChange,
            valueRange = 0f..1f,
            steps = 254,
            colors = AppObjectsColors.sliderColors()
        )
    }
}