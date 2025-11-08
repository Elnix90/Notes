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
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.burnoutcrew.reorderable.ReorderableLazyListState
import org.burnoutcrew.reorderable.detectReorder
import org.elnix.notes.R
import org.elnix.notes.data.NoteEntity
import org.elnix.notes.data.helpers.noteTypeColor
import org.elnix.notes.data.helpers.noteTypeIcon
import org.elnix.notes.data.settings.stores.TagsSettingsStore.getTags
import org.elnix.notes.data.settings.stores.UiSettingsStore
import org.elnix.notes.data.settings.stores.UiSettingsStore.getShowDeleteButton
import org.elnix.notes.data.settings.stores.UiSettingsStore.getShowNoteTypeIcon
import org.elnix.notes.ui.helpers.tags.TagBubble
import org.elnix.notes.ui.theme.LocalExtraColors
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun NoteCard(
    note: NoteEntity,
    isSelectMode: Boolean,
    isReorderMode: Boolean,
    scale: Float,
    elevation: Dp,
    isDragging: Boolean,
    reorderState: ReorderableLazyListState,
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

    ElevatedCard(
        modifier = modifier
            .padding(horizontal = 12.dp)
            .fillMaxWidth()
            .scale(scale)
            .background(note.bgColor, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(elevation),
        colors = CardDefaults.cardColors(containerColor = note.bgColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (!isReorderMode) {
                        Modifier.combinedClickable(
                            onLongClick = { onLongClick() },
                            onClick = { onClick() }
                        )
                    } else Modifier
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
                        .then(
                            if (isSelectMode) {
                                Modifier.pointerInput(Unit) {}
                            } else {
                                Modifier.clickable { onTypeIconClick() }
                            }
                        )
                        .background(iconColor)
                        .padding(5.dp)
                ) {
                    Icon(
                        imageVector = noteTypeIcon(note.type),
                        contentDescription = "Note icon",
                        modifier = Modifier.size(25.dp),
                        tint = MaterialTheme.colorScheme.outline,
                    )
                }

                VerticalDivider(color = MaterialTheme.colorScheme.outline)

            }

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(note.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = note.txtColor.copy(
                        alpha = if (note.isCompleted) 0.3f else 0.6f
                    )
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

            Box(
                modifier = Modifier
                    .requiredSize(40.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isReorderMode) {
                    Icon(
                        imageVector = Icons.Default.DragHandle,
                        contentDescription = stringResource(R.string.reorder),
                        tint = if (isDragging)
                            LocalExtraColors.current.select
                        else
                            MaterialTheme.colorScheme.outline,
                        modifier = Modifier
                            .size(24.dp)
                            .detectReorder(reorderState)
                    )
                } else if (showDeleteButton) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .then(
                                if (isSelectMode) {
                                    Modifier.pointerInput(Unit) {}
                                } else {
                                    Modifier.clickable { onDeleteButtonClick() }
                                }
                            )
                            .padding(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}
