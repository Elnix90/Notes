package org.elnix.notes.ui.helpers.toolbars

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.elnix.notes.R
import org.elnix.notes.data.settings.stores.ToolbarSetting
import org.elnix.notes.ui.helpers.colors.ColorPickerRow
import org.elnix.notes.ui.theme.adjustBrightness

@Composable
fun ToolbarColorSelectorDialog(
    toolbar: ToolbarSetting,
    onDismiss: () -> Unit,
    onValidate: (Int, Int) -> Unit
) {
    val defaultSurfaceColor = MaterialTheme.colorScheme.surface
    val defaultBorderColor = defaultSurfaceColor.adjustBrightness(3f)

    var colorInt by remember {
        mutableIntStateOf(toolbar.color?.toArgb() ?: defaultSurfaceColor.toArgb())
    }

    var borderColorInt by remember {
        mutableIntStateOf(toolbar.borderColor?.toArgb() ?: defaultBorderColor.toArgb())
    }

    AlertDialog(
        containerColor = MaterialTheme.colorScheme.surface,
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.toolbar_colors),
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {

                ColorPickerRow(
                    label = stringResource(R.string.toolbar_color),
                    defaultColor = defaultSurfaceColor,
                    currentColor = colorInt,
                    backgroundColor = MaterialTheme.colorScheme.secondary,
                    onColorPicked = { colorInt = it }
                )

                ColorPickerRow(
                    label = stringResource(R.string.toolbar_border_color),
                    defaultColor = defaultBorderColor,
                    currentColor = borderColorInt,
                    backgroundColor = MaterialTheme.colorScheme.secondary,
                    onColorPicked = { borderColorInt = it }
                )
            }
        },
        confirmButton = {
            Button(onClick = { onValidate(colorInt, borderColorInt) }
            ) {
                Text(
                    text = stringResource(R.string.save),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cancel),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}

