package org.elnix.notes.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class Action { DELETE, COMPLETE, EDIT }

data class ActionSettings(
    val leftAction: Action = Action.DELETE,
    val rightAction: Action = Action.EDIT,
    val clickAction: Action = Action.COMPLETE
)

@Composable
fun actionColor(action: Action): Color = when (action) {
    Action.DELETE -> MaterialTheme.colorScheme.error
    Action.COMPLETE -> MaterialTheme.colorScheme.primary
    Action.EDIT -> MaterialTheme.colorScheme.secondary
}

@Composable
fun actionIcon(action: Action) = when (action) {
    Action.DELETE -> Icons.Default.Delete
    Action.EDIT -> Icons.Default.Edit
    Action.COMPLETE -> Icons.Default.CheckBox
}