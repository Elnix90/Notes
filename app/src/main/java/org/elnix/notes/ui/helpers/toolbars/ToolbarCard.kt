package org.elnix.notes.ui.helpers.toolbars

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp


@Composable
fun ToolbarCard(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surface,
    scrollState: ScrollState,
    ghosted: Boolean = false,
    onGhostClick: (() -> Unit)? = null,
    scale: Float = 1f,
    content: @Composable RowScope.(isScaled: Boolean) -> Unit
) {
    val isScaled = scale != 1f
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
        modifier = modifier
            .then(
                if (ghosted) Modifier.pointerInput(Unit) {
                    detectTapGestures(onTap = { onGhostClick?.invoke() })
                } else Modifier
            )
            .alpha(if (ghosted) 0.6f else 1f)
    ) {
        Row(
            modifier = rowModifier
                .horizontalScroll(scrollState)
                .clip(RoundedCornerShape(16.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            content(isScaled)
            // Burger overflow
            IconButton(onClick = { menuExpanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More")
            }
            DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                DropdownMenuItem(
                    text = { Text("Overflow item") },
                    onClick = { /* overflow item clicked */ }
                )
            }
        }
    }
}
