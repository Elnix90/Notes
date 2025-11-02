package org.elnix.notes.ui.helpers.tags

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import org.elnix.notes.data.helpers.TagItem


@Composable
fun TagBubble(
    tag: TagItem,
    selected: Boolean = true,
    ghostMode: Boolean = false,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .padding(4.dp)
            .border(1.dp, tag.color.copy(if (selected) 1f else 0.3f), CircleShape)
            .clip(CircleShape)
            .then(
                if (ghostMode) {
                    Modifier.pointerInput(Unit) {}
                } else {
                    Modifier.combinedClickable(
                        onLongClick = { onLongClick?.invoke() },
                        onClick = { onClick?.invoke() }
                    )
                }
            )
            .background(tag.color.copy(if (selected) 0.15f else 0.05f), CircleShape)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = tag.name,
            color = tag.color.copy(alpha = if (selected) 1f else 0.4f)
        )

        if (onDelete != null) {
            Spacer(Modifier.width(4.dp))
            IconButton(onClick = onDelete, modifier = Modifier.size(18.dp)) {
                Icon(
                    Icons.Default.Cancel,
                    contentDescription = "Delete Tag",
                    tint = tag.color.copy(alpha = if (selected) 1f else 0.4f)
                )
            }
        }
    }
}
