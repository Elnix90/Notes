package org.elnix.notes

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.elnix.notes.data.NoteEntity
import org.elnix.notes.data.settings.NoteActionSettings
import org.elnix.notes.data.settings.swipeActionColor
import org.elnix.notes.data.settings.swipeActionIcon
import org.elnix.notes.ui.helpers.NoteCard

@Composable
fun NotesList(
    notes: List<NoteEntity>,
    selectedNotes: Set<NoteEntity>,
    isSelectMode: Boolean,
    onNoteClick: (NoteEntity) -> Unit,
    onNoteLongClick: (NoteEntity) -> Unit,
    onRightAction: (NoteEntity) -> Unit,
    onLeftAction: (NoteEntity) -> Unit,
    onButtonClick: (NoteEntity) -> Unit,
    actionSettings: NoteActionSettings
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(notes) { note ->
            SwipeableNoteCard(
                note = note,
                selected = selectedNotes.contains(note),
                isSelectMode = isSelectMode,
                onNoteClick = { onNoteClick(note) },
                onNoteLongClick = { onNoteLongClick(note) },
                onRightAction = { onRightAction(note) },
                onLeftAction = { onLeftAction(note) },
                onButtonClick = { onButtonClick(note) },
                actionSettings = actionSettings
            )
        }
    }
}

@Composable
fun SwipeableNoteCard(
    note: NoteEntity,
    selected: Boolean,
    isSelectMode: Boolean,
    onNoteClick: (NoteEntity) -> Unit,
    onNoteLongClick: (NoteEntity) -> Unit,
    onRightAction: (NoteEntity) -> Unit,
    onLeftAction: (NoteEntity) -> Unit,
    onButtonClick: (NoteEntity) -> Unit,
    actionSettings: NoteActionSettings
) {
    val maxSwipePx = 80f
    var swipeOffset by remember { mutableFloatStateOf(0f) }
    var swipeState by remember { mutableStateOf(SwipeState.Default) }

    val draggableState = rememberDraggableState { delta ->
        swipeOffset = (swipeOffset + delta).coerceIn(-maxSwipePx, maxSwipePx)
        swipeState = when {
            swipeOffset >= maxSwipePx - 20 -> SwipeState.RightAction
            swipeOffset <= -maxSwipePx + 20 -> SwipeState.LeftAction
            else -> SwipeState.Default
        }
    }

    val selectionOffset by animateDpAsState(
        targetValue = if (selected) 40.dp else 0.dp,
        label = "selectionOffset"
    )

    // disable dragging when select mode is active
    val dragModifier = if (!isSelectMode) {
        Modifier.draggable(
            state = draggableState,
            orientation = Orientation.Horizontal,
            onDragStopped = {
                when (swipeState) {
                    SwipeState.LeftAction -> onLeftAction(note)
                    SwipeState.RightAction -> onRightAction(note)
                    else -> {}
                }
                swipeOffset = 0f
                swipeState = SwipeState.Default
            }
        )
    } else {
        Modifier
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .then(dragModifier)
    ) {
        // Swipe background
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    color = when {
                        swipeOffset > 0f -> swipeActionColor(actionSettings.rightAction)
                        swipeOffset < 0f -> swipeActionColor(actionSettings.leftAction)
                        else -> Color.Transparent
                    },
                    shape = RoundedCornerShape(12.dp)
                )
        )

        // Swipe action icon
        if (swipeOffset != 0f) {
            val isActionReady = swipeState != SwipeState.Default
            Icon(
                imageVector = when {
                    swipeOffset > 0f -> swipeActionIcon(actionSettings.rightAction)
                    swipeOffset < 0f -> swipeActionIcon(actionSettings.leftAction)
                    else -> Icons.Default.Delete
                },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .align(if (swipeOffset > 0f) Alignment.CenterStart else Alignment.CenterEnd)
                    .padding(horizontal = 24.dp)
                    .size(if (isActionReady) 30.dp else 24.dp)
            )
        }

        // Selected icon
        if (selected) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 12.dp)
                    .size(26.dp)
            )
        }

        // Foreground Note Card
        NoteCard(
            note = note,
            onClick = { onNoteClick(note) },
            onLongClick = { onNoteLongClick(note) },
            onDeleteButtonClick = { onButtonClick(note) },
            modifier = Modifier
                .offset(x = swipeOffset.dp + selectionOffset)
//                .background(Color.Yellow/*note.color*/, RoundedCornerShape(12.dp))
    )
    }
}
