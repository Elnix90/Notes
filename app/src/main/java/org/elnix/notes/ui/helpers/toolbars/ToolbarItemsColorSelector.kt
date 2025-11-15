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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.elnix.notes.R
import org.elnix.notes.data.helpers.globalActionName
import org.elnix.notes.data.settings.stores.ToolbarItemState
import org.elnix.notes.ui.helpers.colors.ColorPickerRow
import org.elnix.notes.ui.theme.adjustBrightness

@Composable
fun ToolbarItemColorSelectorDialog(
    item: ToolbarItemState,
    defaultIconColor: Color,
    defaultBgColor: Color,
    onDismiss: () -> Unit,
    onValidate: (Int, Int) -> Unit
) {
    val ctx = LocalContext.current

    var onColorInt by remember {
        mutableIntStateOf(item.bgColor?.toArgb() ?: defaultIconColor.toArgb())
    }

    var bgColorInt by remember {
        mutableIntStateOf(item.bgColor?.toArgb() ?: defaultBgColor.toArgb())
    }

    AlertDialog(
        containerColor = MaterialTheme.colorScheme.surface,
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "${globalActionName(ctx,item.action)} ${stringResource(R.string.color)} ",
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                ColorPickerRow(
                    label = stringResource(R.string.toolbar_color),
                    defaultColor = defaultIconColor,
                    currentColor = onColorInt,
                    backgroundColor = MaterialTheme.colorScheme.surface.adjustBrightness(0.7f),
                    onColorPicked = { onColorInt = it}
                )

                ColorPickerRow(
                    label = stringResource(R.string.toolbar_color),
                    defaultColor = defaultBgColor,
                    currentColor = bgColorInt,
                    backgroundColor = MaterialTheme.colorScheme.surface.adjustBrightness(0.7f),
                    onColorPicked = { bgColorInt = it}
                )
            }
        },
        confirmButton = {
            Button(onClick = { onValidate(onColorInt, bgColorInt) }
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

