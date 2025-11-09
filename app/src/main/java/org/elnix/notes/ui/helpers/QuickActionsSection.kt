package org.elnix.notes.ui.helpers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.elnix.notes.R
import org.elnix.notes.data.NoteEntity
import org.elnix.notes.ui.theme.AppObjectsColors
import org.elnix.notes.ui.theme.LocalExtraColors

@Composable
fun QuickActionSection(
    note: NoteEntity,
    onComplete: (Boolean) -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit
) {
    Row (
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier.fillMaxWidth()
    ) {

        CompletionToggle(note) { onComplete(it) }
        Spacer(Modifier.weight(1f))
        IconButton(
            onClick = { onDuplicate() },
            colors = AppObjectsColors.iconButtonColors(
                backgroundColor = LocalExtraColors.current.edit
            )
        ) {
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = stringResource(R.string.duplicate)
            )
        }
        Spacer(Modifier.width(8.dp))

        IconButton(
            onClick = { onDelete() },
            colors = AppObjectsColors.iconButtonColors(
                backgroundColor = LocalExtraColors.current.delete
            )
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(R.string.delete)
            )
        }
    }
}