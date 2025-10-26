package org.elnix.notes.data.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.elnix.notes.ui.theme.LocalExtraColors

enum class SwipeActions { DELETE, COMPLETE, EDIT }

data class SwipeActionSettings(
    val leftAction: SwipeActions = SwipeActions.DELETE,
    val rightAction: SwipeActions = SwipeActions.EDIT,
    val clickAction: SwipeActions = SwipeActions.COMPLETE
)

@Composable
fun swipeActionColor(action: SwipeActions): Color {
    val extras = LocalExtraColors.current
    return when (action) {
        SwipeActions.DELETE -> extras.delete
        SwipeActions.EDIT -> extras.edit
        SwipeActions.COMPLETE -> extras.complete
    }
}

@Composable
fun swipeActionIcon(action: SwipeActions) = when (action) {
    SwipeActions.DELETE -> Icons.Default.Delete
    SwipeActions.EDIT -> Icons.Default.Edit
    SwipeActions.COMPLETE -> Icons.Default.CheckBox
}