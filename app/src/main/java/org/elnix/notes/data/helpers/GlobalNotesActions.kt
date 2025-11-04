package org.elnix.notes.data.helpers

import android.app.ProgressDialog.show
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Reorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SpaceBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import org.elnix.notes.R
import org.elnix.notes.ui.theme.LocalExtraColors

enum class GlobalNotesActions {
    SEARCH, SORT, SETTINGS, DESELECT_ALL, ADD_NOTE, REORDER, EDIT_NOTE, DELETE_NOTE, COMPLETE_NOTE, SPACER
}

@Composable
fun globalActionIcon(action: GlobalNotesActions): ImageVector = when (action) {
    GlobalNotesActions.SEARCH -> Icons.Default.Search
    GlobalNotesActions.SORT -> Icons.AutoMirrored.Filled.Sort
    GlobalNotesActions.SETTINGS -> Icons.Default.Settings
    GlobalNotesActions.DESELECT_ALL -> Icons.Default.Close
    GlobalNotesActions.ADD_NOTE -> Icons.Default.Add
    GlobalNotesActions.REORDER -> Icons.Default.Reorder
    GlobalNotesActions.EDIT_NOTE -> Icons.Default.Edit
    GlobalNotesActions.DELETE_NOTE -> Icons.Default.Delete
    GlobalNotesActions.COMPLETE_NOTE -> Icons.Default.CheckCircle
    GlobalNotesActions.SPACER -> Icons.Default.SpaceBar
}

@Composable
fun globalActionColor(action: GlobalNotesActions): Color {
    val extras = LocalExtraColors.current
    return when (action) {
        GlobalNotesActions.SEARCH -> extras.edit
        GlobalNotesActions.SORT -> extras.complete
        GlobalNotesActions.SETTINGS -> extras.select
        GlobalNotesActions.DESELECT_ALL -> extras.delete
        GlobalNotesActions.ADD_NOTE -> extras.complete
        GlobalNotesActions.REORDER -> extras.edit
        GlobalNotesActions.EDIT_NOTE -> extras.edit
        GlobalNotesActions.DELETE_NOTE -> extras.delete
        GlobalNotesActions.COMPLETE_NOTE -> extras.complete
        GlobalNotesActions.SPACER -> Color.White
    }
}

fun globalActionName(ctx: Context, action: GlobalNotesActions): String = when (action) {
    GlobalNotesActions.SEARCH -> ctx.getString(R.string.search)
    GlobalNotesActions.SORT -> ctx.getString(R.string.sort)
    GlobalNotesActions.SETTINGS -> ctx.getString(R.string.settings)
    GlobalNotesActions.DESELECT_ALL -> ctx.getString(R.string.deselect_all)
    GlobalNotesActions.ADD_NOTE -> ctx.getString(R.string.add_note)
    GlobalNotesActions.REORDER -> ctx.getString(R.string.reorder)
    GlobalNotesActions.EDIT_NOTE -> ctx.getString(R.string.edit)
    GlobalNotesActions.DELETE_NOTE -> ctx.getString(R.string.delete)
    GlobalNotesActions.COMPLETE_NOTE -> ctx.getString(R.string.complete)
    GlobalNotesActions.SPACER -> ctx.getString(R.string.spacer)
}


@Composable
fun GlobalActionIcon(
    ctx: Context,
    action: GlobalNotesActions,
    ghosted: Boolean = false,
    scale: Float = 1f,
    searchExpanded: Boolean = true,
    onClick: (GlobalNotesActions) -> Unit
) {
    val icon = globalActionIcon(action)

    val label = globalActionName(ctx, action)

    val onColor = MaterialTheme.colorScheme.outline
    val bgColor = globalActionColor(action).copy(alpha = 0.4f)

    // Create a clickable modifier that passes clicks through if ghosted
    val clickModifier = if (ghosted) {
        Modifier.pointerInput(Unit) {}
    } else {
        Modifier.clickable { onClick(action) }
    }

    // Determine visual mode: minimal if scale < 1f
    val minimalMode = scale < 1f

    TODO( modifiy this to show the fake text if minimal mode)
    if (minimalMode) {
        // Just a circle representing the icon
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(onColor.copy(alpha = 0.4f), shape = CircleShape)
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(0.5f))
        )
    } else if (action == GlobalNotesActions.SEARCH && searchExpanded) {
        Row(
            modifier = Modifier
                .graphicsLayer { this.scaleX = scale; this.scaleY = scale }
                .background(
                    color = bgColor.copy(alpha = if (ghosted) 0.4f else 1f),
                    shape = CircleShape
                )
                .then(clickModifier)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = onColor
            )
            if (action == GlobalNotesActions.SEARCH && searchExpanded) {
                Spacer(Modifier.width(6.dp))
                if (minimalMode) {
                    // Simple horizontal bar as placeholder for text
                    Box(
                        modifier = Modifier
                            .width(24.dp)
                            .height(3.dp)
                            .background(onColor.copy(alpha = 0.4f), RoundedCornerShape(2.dp))
                    )
                } else {
                    if (!ghosted) {
                        Text(
                            text = label,
                            color = onColor,
                            style = MaterialTheme.typography.labelMedium
                        )
                    } else {
                        // faded text if ghosted but not minimal
                        Text(
                            text = label,
                            color = onColor.copy(alpha = 0.5f),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    } else {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = onColor
        )
    }
}
