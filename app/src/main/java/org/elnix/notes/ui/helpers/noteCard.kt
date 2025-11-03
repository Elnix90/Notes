package org.elnix.notes.ui.helpers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import org.elnix.notes.data.NoteEntity
import org.elnix.notes.data.helpers.noteTypeColor
import org.elnix.notes.data.helpers.noteTypeIcon
import org.elnix.notes.data.settings.stores.TagsSettingsStore.getTags
import org.elnix.notes.data.settings.stores.UiSettingsStore
import org.elnix.notes.data.settings.stores.UiSettingsStore.getShowDeleteButton
import org.elnix.notes.data.settings.stores.UiSettingsStore.getShowNoteTypeIcon
import org.elnix.notes.ui.helpers.tags.TagBubble
import org.elnix.notes.ui.theme.adjustBrightness
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun NoteCard(
    note: NoteEntity,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onDeleteButtonClick: () -> Unit,
    onTypeIconClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val ctx = LocalContext.current

    val showDeleteButton by getShowDeleteButton(ctx).collectAsState(initial = true)
    val showNoteTypeIcon by getShowNoteTypeIcon(ctx).collectAsState(initial = true)

    val showTagsInNotes by UiSettingsStore.getShowTagsInNotes(ctx).collectAsState(initial = true)
    val allTags by getTags(ctx).collectAsState(initial = emptyList())

    val noteTags = remember(allTags, note.tagIds) {
        allTags.filter { note.tagIds.contains(it.id) }
    }

    Card(
        modifier = modifier
            .padding(horizontal = 12.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = note.bgColor
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .combinedClickable(
                    onLongClick = { onLongClick() },
                    onClick = { onClick() }
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (showNoteTypeIcon) {
                val iconColor = noteTypeColor(note.type)

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .clip(CircleShape)
                        .clickable { onTypeIconClick() }
                        .background(note.bgColor.adjustBrightness(if (iconColor.luminance() > 0.5) 0.5f else 1.5f))
                        .padding(5.dp)
                ) {
                    Icon(
                        imageVector = noteTypeIcon(note.type),
                        contentDescription = "Note icon",
                        modifier = Modifier.size(25.dp),
                        tint = iconColor,
                    )
                }

                VerticalDivider(color = MaterialTheme.colorScheme.outline)

            }

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(note.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = note.txtColor.copy(alpha = 0.6f)
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = note.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        textDecoration = if (note.isCompleted) TextDecoration.LineThrough else null
                    ),
                    color = note.txtColor.copy(
                        alpha = if (note.isCompleted) 0.5f else 1f
                    )
                )


                if(showTagsInNotes) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        noteTags.forEach { tag ->
                            TagBubble(
                                tag = tag,
                                ghostMode = true
                            )
                        }
                    }
                }
            }

            if(showDeleteButton) {

                IconButton(
                    onClick = { onDeleteButtonClick() },
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
