package org.elnix.notes.ui.helpers.colors

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.elnix.notes.R
import org.elnix.notes.data.NoteEntity
import org.elnix.notes.ui.theme.AppObjectsColors

@Composable
fun NotesColorPickerSection(
    note: NoteEntity,
    onBgColorPicked: (Int) -> Unit,
    onTextColorPicked: (Int) -> Unit,
    onAutoSwitchToggle: (Boolean) -> Unit,
    onRandomColorClick: () -> Unit,
) {
    // --- Colors section  ---
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // NOTE BACKGROUND COLOR PICKER
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val label = stringResource(R.string.note_color)

                Text(
                    text = label,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.labelSmall
                )
                Spacer(Modifier.height(4.dp))

                ColorPickerRow(
                    label = label,
                    showLabel = false,
                    defaultColor = MaterialTheme.colorScheme.surface,
                    currentColor = note.bgColor?.toArgb()
                        ?: MaterialTheme.colorScheme.surface.toArgb(),
                    randomColorButton = false
                ) { onBgColorPicked(it) }

                // RANDOM NOTE COLOR BUTTON
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = { onRandomColorClick() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shuffle,
                            contentDescription = stringResource(R.string.random_note_color),
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                    Text(
                        text = stringResource(R.string.random_note_color),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // TEXT COLOR PICKER
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val label = stringResource(R.string.text_color)
                Text(
                    text = label,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.labelSmall
                )
                Spacer(Modifier.height(4.dp))

                val autoTextColorEnabled = note.autoTextColor

                ColorPickerRow(
                    label = label,
                    showLabel = false,
                    defaultColor = MaterialTheme.colorScheme.onSurface,
                    currentColor = note.txtColor?.toArgb()
                        ?: MaterialTheme.colorScheme.onSurface.toArgb(),
                    enabled = !autoTextColorEnabled,
                    randomColorButton = false
                ) { onTextColorPicked(it) }

                // AUTO TEXT COLOR CHECKBOX
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Checkbox(
                        checked = autoTextColorEnabled,
                        onCheckedChange = { onAutoSwitchToggle(it) },
                        colors = AppObjectsColors.checkboxColors()
                    )
                    Text(
                        text = stringResource(R.string.auto_text_color),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}