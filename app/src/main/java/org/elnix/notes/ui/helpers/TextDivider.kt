package org.elnix.notes.ui.helpers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun TextDivider(
    text: String,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.outline,
    textColor: Color = MaterialTheme.colorScheme.outline,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    enabled: Boolean = true,
    thickness: Dp = 1.dp,
    padding: Dp = 8.dp
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f).clip(CircleShape),
            color = lineColor.copy(if (enabled) 1f else 0.5f),
            thickness = thickness
        )
        Text(
            text = text,
            color = textColor.copy(if (enabled) 1f else 0.5f),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = padding)
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f).clip(CircleShape),
            color = lineColor.copy(if (enabled) 1f else 0.5f),
            thickness = thickness
        )
    }
}
