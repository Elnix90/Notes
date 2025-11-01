package org.elnix.notes.ui.helpers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.elnix.notes.data.settings.NotesActions

@Composable
fun MultiSelectToolbar(
    isSingleSelected: Boolean,
    onGroupAction: (NotesActions) -> Unit,
    onCloseSelection: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        IconButton(onClick = { onCloseSelection() }) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel selection")
        }

        Spacer(Modifier.weight(1f))

        if (isSingleSelected) {
            IconButton(onClick = { onGroupAction(NotesActions.EDIT) }) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit selected note")
            }
        }

        IconButton(onClick = { onGroupAction(NotesActions.DELETE) }) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Selected")
        }

        IconButton(onClick = { onGroupAction(NotesActions.COMPLETE) }) {
            Icon(imageVector = Icons.Default.CheckBox, contentDescription = "Mark Complete")
        }
    }
}
