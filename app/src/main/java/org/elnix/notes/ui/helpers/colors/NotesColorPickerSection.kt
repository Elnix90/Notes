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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.elnix.notes.R
import org.elnix.notes.data.NoteEntity
import org.elnix.notes.ui.theme.AppObjectsColors

@Composable
fun NotesColorPickerSection(
    note: NoteEntity,
    onBgColorPicked: (Color) -> Unit,
    onTextColorPicked: (Color) -> Unit,
    onAutoSwitchToggle: (Boolean) -> Unit
) {
    val autoTextColorEnabled = note.autoTextColor

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // NOTE BACKGROUND COLOR PICKER
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
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
                    currentColor = note.bgColor
                        ?: MaterialTheme.colorScheme.surface,
                    backgroundColor = MaterialTheme.colorScheme.primary,
                    randomColorButton = true
                ) { onBgColorPicked(it) }
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


                ColorPickerRow(
                    label = label,
                    showLabel = false,
                    defaultColor = MaterialTheme.colorScheme.onSurface,
                    currentColor = note.txtColor
                        ?: MaterialTheme.colorScheme.onSurface,
                    enabled = !autoTextColorEnabled,
                    backgroundColor = MaterialTheme.colorScheme.primary,
                    randomColorButton = false
                ) { onTextColorPicked(it) }
            }
        }

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