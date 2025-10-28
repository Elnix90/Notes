package org.elnix.notes.ui.helpers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.elnix.notes.ui.theme.adjustBrightness


@Composable
fun <T> ActionSelectorRow(
    options: List<T>,
    selected: T,
    enabled: Boolean = true,
    label: String? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    surfaceColor: Color = MaterialTheme.colorScheme.surface,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    optionLabel: (T) -> String = { it.toString() },
    onSelected: (T) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (label != null) {
            Arrangement.SpaceBetween
        } else {
            Arrangement.Center
        },
        modifier = Modifier
            .apply{
                if (label != null) {
                    Modifier.fillMaxWidth()
                } else {
                    Modifier.wrapContentWidth()
                }
            }
            .background(
                color = backgroundColor.adjustBrightness(if (enabled) 1f else 0.5f),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(enabled) { showDialog = true }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        if (label != null) {
            Text(
                text = label,
                color = textColor.adjustBrightness(if (enabled) 1f else 0.5f)
            )
        }

        Text(
            text = optionLabel(selected),
            color = textColor.adjustBrightness(if (enabled) 1f else 0.5f),
            style = MaterialTheme.typography.labelSmall,
    )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {},
            dismissButton = {},
            title = {},
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    options.forEach { option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp)
                                .clickable {
                                    onSelected(option)
                                    showDialog = false
                                }
                        ) {
                            RadioButton(
                                selected = (selected == option),
                                onClick = {
                                    onSelected(option)
                                    showDialog = false
                                }
                            )
                            Text(
                                text = optionLabel(option),
                                color = textColor
                            )
                        }
                    }
                }
            },
            containerColor = surfaceColor,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
