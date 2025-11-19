package org.elnix.notes.ui.helpers.colors

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.elnix.notes.R
import org.elnix.notes.ui.theme.AppObjectsColors
import org.elnix.notes.ui.theme.adjustBrightness

/**
 * Generic color selector dialog for multiple color entries
 * @param titleDialog Title of the dialog
 * @param entries List of colors to edit
 * @param onDismiss Called when user cancels
 * @param onValidate Returns a list of updated color ints in the same order as entries
 */
@Composable
fun UnifiedColorsSelectorDialog(
    titleDialog: String,
    entries: List<ColorSelectorEntry>,
    onDismiss: () -> Unit,
    onValidate: (List<Int>) -> Unit
) {
    // State for each color
    val colorStates = remember { entries.map { mutableIntStateOf(it.initialColor.toArgb()) } }

    AlertDialog(
        containerColor = MaterialTheme.colorScheme.surface,
        onDismissRequest = onDismiss,
        title = { Text(text = titleDialog, color = MaterialTheme.colorScheme.onSurface) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                entries.forEachIndexed { index, entry ->
                    ColorPickerRow(
                        label = entry.label,
                        defaultColor = entry.defaultColor,
                        currentColor = colorStates[index].intValue,
                        backgroundColor = MaterialTheme.colorScheme.surface.adjustBrightness(0.7f),
                        onColorPicked = { colorStates[index].intValue = it }
                    )
                }
                OutlinedButton(
                    onClick = {
                        onValidate(entries.map { it.initialColor.toArgb() } )
                    },
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                    colors = AppObjectsColors.cancelButtonColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        text = stringResource(R.string.reset_to_default_colors),
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { onValidate(colorStates.map { it.intValue }) }) {
                Text(
                    text = "Save",
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}

/**
 * Represents one color entry in the multi-color dialog
 */
data class ColorSelectorEntry(
    val label: String,
    val defaultColor: Color,
    val initialColor: Color
)
