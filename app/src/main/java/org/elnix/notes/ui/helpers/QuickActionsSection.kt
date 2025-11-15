package org.elnix.notes.ui.helpers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.elnix.notes.data.NoteEntity
import org.elnix.notes.data.helpers.GlobalActionIcon
import org.elnix.notes.data.helpers.GlobalNotesActions

@Composable
fun QuickActionSection(
    note: NoteEntity,
    onComplete: (Boolean) -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit
) {
    val ctx = LocalContext.current

    Row (
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier.fillMaxWidth()
    ) {

        CompletionToggle(note) { onComplete(it) }

        Spacer(Modifier.weight(1f))

        GlobalActionIcon(
            ctx = ctx,
            action = GlobalNotesActions.DUPLICATE_NOTE,
            onColor = null,
            bgColor = null,
            showButtonLabel = false
        ) { onDuplicate() }

        Spacer(Modifier.width(8.dp))

        GlobalActionIcon(
            ctx = ctx,
            action = GlobalNotesActions.DELETE_NOTE,
            onColor = null,
            bgColor = null,
            showButtonLabel = false
        ) { onDelete() }


    }
}