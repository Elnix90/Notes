package org.elnix.notes.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.elnix.notes.ui.theme.LocalExtraColors

enum class Action { DELETE, COMPLETE, EDIT }

data class ActionSettings(
    val leftAction: Action = Action.DELETE,
    val rightAction: Action = Action.EDIT,
    val clickAction: Action = Action.COMPLETE
)

@Composable
fun actionColor(action: Action): Color {
    val extras = LocalExtraColors.current
    return when (action) {
        Action.DELETE -> extras.delete
        Action.EDIT -> extras.edit
        Action.COMPLETE -> extras.complete
    }
}

@Composable
fun actionIcon(action: Action) = when (action) {
    Action.DELETE -> Icons.Default.Delete
    Action.EDIT -> Icons.Default.Edit
    Action.COMPLETE -> Icons.Default.CheckBox
}