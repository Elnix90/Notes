package org.elnix.notes.ui.helpers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.elnix.notes.ui.theme.AppObjectsColors

@Composable
fun SwitchRow(
    state: Boolean?,
    text: String,
    enabled: Boolean = true,
    defaultValue: Boolean = false,
    onCheck: (Boolean) -> Unit
) {
    val checked = state ?: defaultValue

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled) { onCheck(!checked) }
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = if (enabled) 1f else 0.5f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = text, color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (enabled) 1f else 0.5f))
        Switch(
            checked = checked,
            enabled = enabled,
            onCheckedChange = { onCheck(it) },
            colors = AppObjectsColors.switchColors()
        )
    }
}
