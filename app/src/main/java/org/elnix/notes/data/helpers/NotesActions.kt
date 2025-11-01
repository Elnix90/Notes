package org.elnix.notes.data.helpers

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.elnix.notes.ui.theme.LocalExtraColors

enum class NotesActions { DELETE, COMPLETE, EDIT, SELECT }

data class NoteActionSettings(
    val leftAction: NotesActions = NotesActions.DELETE,
    val rightAction: NotesActions = NotesActions.EDIT,
    val clickAction: NotesActions = NotesActions.COMPLETE,
    val longClickAction: NotesActions = NotesActions.SELECT,
    val selectAction: NotesActions = NotesActions.SELECT,
)

@Composable
fun noteActionColor(action: NotesActions): Color {
    val extras = LocalExtraColors.current
    return when (action) {
        NotesActions.DELETE -> extras.delete
        NotesActions.EDIT -> extras.edit
        NotesActions.COMPLETE -> extras.complete
        NotesActions.SELECT -> extras.select
    }
}

@Composable
fun noteActionIcon(action: NotesActions) = when (action) {
    NotesActions.DELETE -> Icons.Default.Delete
    NotesActions.EDIT -> Icons.Default.Edit
    NotesActions.COMPLETE -> Icons.Default.CheckBox
    NotesActions.SELECT -> Icons.Default.CheckCircle
}