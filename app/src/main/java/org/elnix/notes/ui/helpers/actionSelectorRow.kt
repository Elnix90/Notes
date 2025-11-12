package org.elnix.notes.ui.helpers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.elnix.notes.ui.theme.AppObjectsColors
import org.elnix.notes.ui.theme.adjustBrightness


@Composable
fun <T> ActionSelectorRow(
    options: List<T>,
    selected: T,
    enabled: Boolean = true,
    switchEnabled: Boolean = true,
    label: String? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    surfaceColor: Color = MaterialTheme.colorScheme.surface,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    optionLabel: (T) -> String = { it.toString() },
    onToggle: ((Boolean) -> Unit)? = null,
    toggled: Boolean? = null,
    onSelected: (T) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    val baseModifier = if (label != null) Modifier.fillMaxWidth() else Modifier.wrapContentWidth()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (label != null) Arrangement.SpaceBetween else Arrangement.Center,
        modifier = baseModifier
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
                color = textColor.adjustBrightness(if (enabled) 1f else 0.5f),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
        }

        Text(
            text = optionLabel(selected),
            color = textColor.adjustBrightness(if (enabled) 1f else 0.5f),
            style = MaterialTheme.typography.labelLarge
        )

        // Right side toggle + divider wrapped in a clickable container
        if (onToggle != null && toggled != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable(enabled = switchEnabled) { onToggle(!toggled) }
            ) {
                VerticalDivider(
                    modifier = Modifier
                        .height(50.dp)
                        .padding(horizontal = 8.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
                    thickness = 1.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = toggled,
                    enabled = switchEnabled,
                    onCheckedChange = { onToggle(it) },
                    colors = AppObjectsColors.switchColors()
                )
            }
        }
    }

    // Options dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {},
            dismissButton = {},
            title = {
                if (label != null) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleMedium,
                        color = textColor
                    )
                }
            },
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
                                },
                                colors = AppObjectsColors.radioButtonColors()
                            )
                            Text(
                                text = optionLabel(option),
                                color = textColor,
                                style = MaterialTheme.typography.bodyMedium
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

