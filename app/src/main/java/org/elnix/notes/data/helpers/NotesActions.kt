package org.elnix.notes.data.helpers

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.elnix.notes.R
import org.elnix.notes.ui.theme.LocalExtraColors

enum class NotesActions { DELETE, COMPLETE, EDIT, SELECT, DUPLICATE, NONE }

    data class NoteActionSettings(
    val leftAction: NotesActions = NotesActions.DELETE,
    val rightAction: NotesActions = NotesActions.EDIT,
    val clickAction: NotesActions = NotesActions.COMPLETE,
    val longClickAction: NotesActions = NotesActions.SELECT,
    val rightButtonAction: NotesActions = NotesActions.DELETE,
    val leftButtonAction: NotesActions = NotesActions.DELETE,
)

@Composable
fun noteActionColor(action: NotesActions): Color {
    val extras = LocalExtraColors.current
    return when (action) {
        NotesActions.DELETE -> extras.delete
        NotesActions.EDIT -> extras.edit
        NotesActions.COMPLETE -> extras.complete
        NotesActions.SELECT -> extras.select
        NotesActions.DUPLICATE -> extras.complete
        NotesActions.NONE -> Color.Transparent
    }
}


fun noteActionName(ctx: Context, action: NotesActions) =  when (action) {
    NotesActions.DELETE -> ctx.getString(R.string.delete)
    NotesActions.EDIT -> ctx.getString(R.string.edit)
    NotesActions.COMPLETE -> ctx.getString(R.string.complete)
    NotesActions.SELECT -> ctx.getString(R.string.select)
    NotesActions.DUPLICATE -> ctx.getString(R.string.duplicate)
    NotesActions.NONE -> ""
}

@Composable
fun noteActionIcon(action: NotesActions) = when (action) {
    NotesActions.DELETE -> Icons.Default.Delete
    NotesActions.EDIT -> Icons.Default.Edit
    NotesActions.COMPLETE -> Icons.Default.CheckBox
    NotesActions.SELECT -> Icons.Default.CheckCircle
    NotesActions.DUPLICATE -> Icons.Default.ContentCopy
    NotesActions.NONE -> Icons.Default.QuestionMark
}