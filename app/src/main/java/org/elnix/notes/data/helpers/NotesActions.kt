package org.elnix.notes.data.helpers

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.elnix.notes.R
import org.elnix.notes.ui.theme.LocalExtraColors

enum class NotesActions { DELETE, COMPLETE, EDIT, SELECT }

data class NoteActionSettings(
    val leftAction: NotesActions = NotesActions.DELETE,
    val rightAction: NotesActions = NotesActions.EDIT,
    val clickAction: NotesActions = NotesActions.COMPLETE,
    val longClickAction: NotesActions = NotesActions.SELECT,
    val typeButtonAction: NotesActions = NotesActions.EDIT,
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


fun noteActionName(ctx: Context, action: NotesActions): String {
    return when (action) {
        NotesActions.DELETE -> ctx.getString(R.string.delete)
        NotesActions.EDIT -> ctx.getString(R.string.edit)
        NotesActions.COMPLETE -> ctx.getString(R.string.complete)
        NotesActions.SELECT -> ctx.getString(R.string.select)
    }
}

@Composable
fun noteActionIcon(action: NotesActions) = when (action) {
    NotesActions.DELETE -> Icons.Default.Delete
    NotesActions.EDIT -> Icons.Default.Edit
    NotesActions.COMPLETE -> Icons.Default.CheckBox
    NotesActions.SELECT -> Icons.Default.CheckCircle
}