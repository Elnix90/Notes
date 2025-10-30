package org.elnix.notes.ui.helpers

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.elnix.notes.R
import org.elnix.notes.ui.theme.AppObjectsColors
import org.elnix.notes.ui.theme.adjustBrightness
import kotlin.random.Random

@Composable
fun SliderColorPicker(
    initialColor: Color,
    defaultColor: Color,
    onColorSelected: (Color) -> Unit
) {
    var red by remember { mutableFloatStateOf(initialColor.red) }
    var green by remember { mutableFloatStateOf(initialColor.green) }
    var blue by remember { mutableFloatStateOf(initialColor.blue) }
    var alpha by remember { mutableFloatStateOf(initialColor.alpha) }

    val previousColors = remember { mutableStateListOf<Color>() }
    val color = Color(red, green, blue, alpha)
    val canPopLastColor = previousColors.isNotEmpty()


    var hexText by remember {
        mutableStateOf(toHexWithAlpha(color))
    }
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
            hexText = toHexWithAlpha(color)
        }
    }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

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
                    red = Random.nextFloat()
                    green = Random.nextFloat()
                    blue = Random.nextFloat()
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
        // --- Preview box ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(color)
                .border(1.dp, MaterialTheme.colorScheme.outline),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = hexText,
                color = if (color.luminance() > 0.4) Color.Black else Color.White
            )
        }


        SliderWithLabel(label = "${stringResource(R.string.red)} :", value = red, color = Color.Red) {
            red = it
            pushCurrentColor()
            hexText = toHexWithAlpha(color)
        }
        SliderWithLabel(label = "${stringResource(R.string.green)} :", value = green, color = Color.Green) {
            green = it
            pushCurrentColor()
            hexText = toHexWithAlpha(color)
        }
        SliderWithLabel(label = "${stringResource(R.string.blue)} :", value = blue, color = Color.Blue) {
            blue = it
            pushCurrentColor()
            hexText = toHexWithAlpha(color)
        }

        SliderWithLabel(
            label = stringResource(R.string.transparency),
            showValue = false,
            value = alpha,
            color = MaterialTheme.colorScheme.primary
        ) {
            alpha = it
            pushCurrentColor()
            hexText = toHexWithAlpha(color)
        }


        Spacer(Modifier.height(12.dp))
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ){
            Button(
                onClick = { onColorSelected(color) },
                modifier = Modifier.weight(3f),
                colors = AppObjectsColors.buttonColors()
            ) {
                Text(
                    text = stringResource(R.string.apply),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }


            OutlinedButton(
                onClick = {
                    red = defaultColor.red
                    green = defaultColor.green
                    blue = defaultColor.blue
                    alpha = defaultColor.alpha
                },
                modifier = Modifier.weight(2f),
                colors = AppObjectsColors.cancelButtonColors(MaterialTheme.colorScheme.surface)
            ) {
                Text(
                    text = stringResource(R.string.reset),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
