package org.elnix.notes.data.helpers

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Reorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import org.elnix.notes.R
import org.elnix.notes.ui.theme.LocalExtraColors

enum class GlobalNotesActions {
    SEARCH, SORT, SETTINGS, DESELECT_ALL, ADD_NOTE, REORDER, EDIT_NOTE, DELETE_NOTE, COMPLETE_NOTE, TAG_FILTER, ADD_TAG, TAGS, SPACER1, SPACER2, SPACER3
}


fun neededCLickTypeForAction (action: GlobalNotesActions): List<ClickType>? = when (action)  {
    GlobalNotesActions.SEARCH -> listOf(ClickType.NORMAL)
    GlobalNotesActions.SORT -> listOf(ClickType.NORMAL)
    GlobalNotesActions.SETTINGS -> listOf(ClickType.NORMAL)
    GlobalNotesActions.DESELECT_ALL -> listOf(ClickType.NORMAL, ClickType.LONG)
    GlobalNotesActions.ADD_NOTE -> listOf(ClickType.NORMAL)
    GlobalNotesActions.REORDER -> listOf(ClickType.NORMAL)
    GlobalNotesActions.EDIT_NOTE -> listOf(ClickType.NORMAL)
    GlobalNotesActions.DELETE_NOTE -> listOf(ClickType.NORMAL)
    GlobalNotesActions.COMPLETE_NOTE -> listOf(ClickType.NORMAL)
    GlobalNotesActions.TAG_FILTER -> listOf(ClickType.NORMAL, ClickType.LONG)
    GlobalNotesActions.ADD_TAG -> listOf(ClickType.NORMAL)
    GlobalNotesActions.TAGS -> listOf(ClickType.NORMAL)
    GlobalNotesActions.SPACER1,
    GlobalNotesActions.SPACER2,
    GlobalNotesActions.SPACER3 -> null
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
    GlobalNotesActions.TAG_FILTER -> Icons.Default.SelectAll
    GlobalNotesActions.ADD_TAG -> Icons.Default.AddCircle
    else -> Icons.Default.QuestionMark
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
        GlobalNotesActions.TAG_FILTER -> extras.select
        GlobalNotesActions.ADD_TAG -> extras.edit
        else -> MaterialTheme.colorScheme.outline
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
    GlobalNotesActions.TAG_FILTER -> ctx.getString(R.string.reset_filters)
    GlobalNotesActions.ADD_TAG -> ctx.getString(R.string.add_tag)
    else -> ctx.getString(R.string.spacer)
}


@Composable
fun GlobalActionIcon(
    ctx: Context,
    color: Color?,
    action: GlobalNotesActions,
    ghosted: Boolean = false,
    scale: Float = 1f,
    showButtonLabel: Boolean = true,
    onLongClick: ((GlobalNotesActions) -> Unit)? = null,
    onDoubleClick: ((GlobalNotesActions) -> Unit)? = null,
    onClick: (GlobalNotesActions) -> Unit
) {
    val icon = globalActionIcon(action)

    val label = globalActionName(ctx, action)

    val onColor = MaterialTheme.colorScheme.outline
    val bgColor = color ?: globalActionColor(action)

    // Create a clickable modifier that passes clicks through if ghosted
    val clickModifier = if (ghosted) {
        Modifier.pointerInput(Unit) {}
    } else {
         when {
            onDoubleClick != null && onLongClick != null -> Modifier.combinedClickable(
                onClick = { onClick(action) },
                onLongClick = { onLongClick(action) },
                onDoubleClick = { onDoubleClick(action) }
            )
            onDoubleClick != null -> Modifier.combinedClickable(
                onClick = { onClick(action) },
                onDoubleClick = { onDoubleClick(action) }
            )
            onLongClick != null -> Modifier.combinedClickable(
                onClick = { onClick(action) },
                onLongClick = { onLongClick(action) }
            )
            else -> Modifier.clickable { onClick(action) }
        }
    }

    // Determine visual mode: minimal if scale < 1f
    val minimalMode = scale < 1f

    Box(
        modifier = Modifier
            .graphicsLayer { this.scaleX = scale; this.scaleY = scale }
            .clip(CircleShape)
            .background(bgColor)
            .then(clickModifier)
            .padding(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!minimalMode) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = onColor
                )
            }
            if (showButtonLabel){
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
                    Text(
                        text = label,
                        color = onColor,
                        style = MaterialTheme.typography.labelMedium
                    )
                    Spacer(Modifier.width(6.dp))
                }
            }
        }
    }
}