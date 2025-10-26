package org.elnix.notes.ui.helpers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.elnix.notes.ui.theme.AppObjectsColors

@Composable
fun SliderWithLabel( value: Float, color: Color, onChange: (Float) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = (value * 255).toInt().toString() ,
            color = color
        )

        Slider(
            value = value,
            onValueChange = onChange,
            valueRange = 0f..1f,
            steps = 254,
            colors = AppObjectsColors.sliderColors(color)
        )
    }
}