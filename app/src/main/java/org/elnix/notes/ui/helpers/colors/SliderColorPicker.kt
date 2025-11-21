package org.elnix.notes.ui.helpers.colors

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.elnix.notes.R
import org.elnix.notes.ui.helpers.SliderWithLabel
import org.elnix.notes.ui.theme.adjustBrightness
import org.elnix.notes.utils.randomColor

@Composable
fun SliderColorPicker(
    actualColor: Color,
    onColorSelected: (Color) -> Unit
) {
    var red by remember { mutableFloatStateOf(actualColor.red) }
    var green by remember { mutableFloatStateOf(actualColor.green) }
    var blue by remember { mutableFloatStateOf(actualColor.blue) }
    var alpha by remember { mutableFloatStateOf(actualColor.alpha) }

    val previousColors = remember { mutableStateListOf<Color>() }
    val color = Color(red, green, blue, alpha)
    val canPopLastColor = previousColors.isNotEmpty()



    fun pushCurrentColor() {
        previousColors.add(color)
    }

    fun popLastColor() {
        if (canPopLastColor) {
            val last = previousColors.removeAt(previousColors.lastIndex)
            red = last.red
            green = last.green
            blue = last.blue
            alpha = last.alpha
            onColorSelected(color)
        }
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(5.dp)
    ) {


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = { popLastColor() },
                modifier = Modifier.weight(1f),
                enabled = canPopLastColor
            ) {
                Icon(
                    Icons.Default.Replay,
                    contentDescription = stringResource(R.string.reset),
                    tint = MaterialTheme.colorScheme.onSurface.adjustBrightness(if (canPopLastColor) 1f else 0.5f)
                )
            }

            IconButton(
                onClick = {
                    pushCurrentColor()
                    val color = randomColor()
                    red = color.red
                    green = color.green
                    blue = color.blue
                    onColorSelected(color)
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    Icons.Default.Shuffle,
                    contentDescription = "Random Color",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        SliderWithLabel(
            label = "${stringResource(R.string.red)} :",
            value = red,
            color = Color.Red
        ) {
            red = it
            pushCurrentColor()
            onColorSelected(color)
        }
        SliderWithLabel(
            label = "${stringResource(R.string.green)} :",
            value = green,
            color = Color.Green
        ) {
            green = it
            pushCurrentColor()
            onColorSelected(color)
        }
        SliderWithLabel(
            label = "${stringResource(R.string.blue)} :",
            value = blue,
            color = Color.Blue
        ) {
            blue = it
            pushCurrentColor()
            onColorSelected(color)
        }

        SliderWithLabel(
            label = stringResource(R.string.transparency),
            showValue = false,
            value = alpha,
            color = MaterialTheme.colorScheme.primary
        ) {
            alpha = it
            pushCurrentColor()
            onColorSelected(color)
        }

    }
}
