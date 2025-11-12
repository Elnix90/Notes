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
import org.elnix.notes.data.helpers.NotesActions
import org.elnix.notes.data.helpers.noteActionColor
import org.elnix.notes.data.helpers.noteActionIcon
import org.elnix.notes.data.helpers.noteActionName
import org.elnix.notes.data.helpers.noteTypeColor
import org.elnix.notes.data.helpers.noteTypeIcon
import org.elnix.notes.data.settings.stores.TagsSettingsStore.getTags
import org.elnix.notes.data.settings.stores.UiSettingsStore
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
    leftAction: NotesActions,
    rightAction: NotesActions,
    onClick: (() -> Unit)?,
    onLongClick: (() -> Unit)?,
    onRightButtonClick: () -> Unit,
    onLeftButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val ctx = LocalContext.current

    val showTagsInNotes by UiSettingsStore.getShowTagsInNotes(ctx).collectAsState(initial = true)
    val allTags by getTags(ctx).collectAsState(initial = emptyList())

    val noteTags = remember(allTags, note.tagIds) {
        allTags.filter { note.tagIds.contains(it.id) }
    }

    val noteBgColor = note.bgColor?: MaterialTheme.colorScheme.surface
    val noteTextColor = note.txtColor ?: MaterialTheme.colorScheme.onSurface
    val bgColor =
        if (isDragging) noteBgColor.copy(alpha = 0.2f)
        else noteBgColor

    ElevatedCard(
        modifier = modifier
            .padding(horizontal = 12.dp)
            .fillMaxWidth()
            .scale(scale)
            .background(bgColor, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(elevation),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (!isReorderMode) {
                        Modifier.combinedClickable(
                            enabled = onClick != null || onLongClick != null,
                            onLongClick = { onLongClick?.invoke() },
                            onClick = { onClick?.invoke() }
                        )
                    } else Modifier
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (leftAction != NotesActions.NONE) {
                val leftIconColor = when(leftAction){
                    NotesActions.EDIT -> noteTypeColor(note.type)
                    else -> noteActionColor(leftAction)
                }
                val leftIcon = when(leftAction){
                    NotesActions.EDIT -> noteTypeIcon(note.type)
                    else -> noteActionIcon(leftAction)
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .clip(CircleShape)
                        .then(
                            if (isSelectMode) {
                                Modifier.pointerInput(Unit) {}
                            } else {
                                Modifier.clickable { onLeftButtonClick() }
                            }
                        )
                        .background(leftIconColor)
                        .padding(5.dp)
                ) {
                    Icon(
                        imageVector = leftIcon,
                        contentDescription = noteActionName(ctx, leftAction),
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
                    color = noteTextColor.copy(
                        alpha = if (note.isCompleted) 0.3f else 0.6f
                    )
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = note.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        textDecoration = if (note.isCompleted) TextDecoration.LineThrough else null
                    ),
                    color = noteTextColor.copy(
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
                } else if (rightAction != NotesActions.NONE) {
                    val rightIconColor = when (rightAction) {
                        NotesActions.EDIT -> noteTypeColor(note.type)
                        else -> noteActionColor(rightAction)
                    }
                    val rightIcon = when (rightAction) {
                        NotesActions.EDIT -> noteTypeIcon(note.type)
                        else -> noteActionIcon(rightAction)
                    }

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .then(
                                if (isSelectMode) {
                                    Modifier.pointerInput(Unit) {}
                                } else {
                                    Modifier.clickable { onRightButtonClick() }
                                }
                            )
                            .background(rightIconColor)
                            .padding(5.dp)
                    ) {
                        Icon(
                            imageVector = rightIcon,
                            contentDescription = noteActionName(ctx, rightAction),
                            modifier = Modifier.size(25.dp),
                            tint = MaterialTheme.colorScheme.outline,
                        )
                    }
                }
            }
        }
    }
}
