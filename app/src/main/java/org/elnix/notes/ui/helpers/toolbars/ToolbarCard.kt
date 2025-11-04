package org.elnix.notes.ui.helpers.toolbars

import android.content.Context
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import org.elnix.notes.data.helpers.GlobalActionIcon
import org.elnix.notes.data.helpers.GlobalNotesActions
import org.elnix.notes.data.helpers.ToolBars


@Composable
fun ToolbarCard(
    ctx: Context,
    toolbar: ToolBars,
    actions: List<GlobalNotesActions>,
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surface,
    ghosted: Boolean = false,
    scale: Float = 1f,
    onActionClick: (GlobalNotesActions) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val rowModifier = Modifier
        .padding(8.dp)
        .graphicsLayer {
            this.scaleX = scale
            this.scaleY = scale
        }
        .background(
            color = if (ghosted) Color.Transparent else color,
            shape = RoundedCornerShape(16.dp)
        )
        .padding(horizontal = 8.dp, vertical = 4.dp)

    Box(
        modifier = modifier.alpha(if (ghosted) 0.6f else 1f)
    ) {
        Row(
            modifier = rowModifier
                .horizontalScroll(scrollState)
                .clip(RoundedCornerShape(16.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            actions.forEach { action ->
                GlobalActionIcon(
                    ctx = ctx,
                    action = action,
                    ghosted = ghosted,
                    scale = scale,
                    searchExpanded = true,
                    onClick = onActionClick
                )
            }


//            IconButton(onClick = { menuExpanded = true }) {
//                Icon(Icons.Default.MoreVert, contentDescription = "More")
//            }
//            DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
//                DropdownMenuItem(
//                    text = { Text("Overflow item") },
//                    onClick = { /* overflow item clicked */ }
//                )
//            }
        }
    }
}

