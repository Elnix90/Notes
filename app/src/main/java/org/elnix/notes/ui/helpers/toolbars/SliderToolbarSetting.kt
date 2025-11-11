package org.elnix.notes.ui.helpers.toolbars

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.elnix.notes.R
import org.elnix.notes.ui.theme.AppObjectsColors
import kotlin.math.roundToInt

@Composable
fun SliderToolbarSetting(
    label: @Composable (Int) -> String,
    initialValue: Int,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    onReset: () -> Unit,
    onValueChangeFinished: (Int) -> Unit
) {
    var currentValue by remember { mutableIntStateOf(initialValue) }

    LaunchedEffect(initialValue) {
        currentValue = initialValue
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label(currentValue),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 16.dp)
            )
            Spacer(Modifier.weight(1f))
            IconButton(onClick = onReset) {
                Icon(
                    imageVector = Icons.Default.Restore,
                    contentDescription = stringResource(R.string.reset),
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }

        Slider(
            value = currentValue.toFloat(),
            onValueChange = { newValue ->
                currentValue = newValue.roundToInt()
            },
            onValueChangeFinished = { onValueChangeFinished(currentValue) },
            valueRange = valueRange,
            steps = steps,
            colors = AppObjectsColors.sliderColors(
                backgroundColor = MaterialTheme.colorScheme.background
            ),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}