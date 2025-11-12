package org.elnix.notes.ui.helpers

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.burnoutcrew.reorderable.ReorderableLazyListState
import org.elnix.notes.SwipeState
import org.elnix.notes.data.NoteEntity
import org.elnix.notes.data.helpers.NoteActionSettings
import org.elnix.notes.data.helpers.NotesActions
import org.elnix.notes.data.helpers.noteActionColor
import org.elnix.notes.data.helpers.noteActionIcon

@Composable
fun SwipeableNoteCard(
    note: NoteEntity,
    selected: Boolean,
    isSelectMode: Boolean,
    isReorderMode: Boolean,
    scale: Float,
    elevation: Dp,
    isDragging: Boolean,
    reorderState: ReorderableLazyListState,
    leftAction: NotesActions,
    rightAction: NotesActions,
    onNoteClick: ((NoteEntity) -> Unit)?,
    onNoteLongClick: ((NoteEntity) -> Unit)?,
    onRightAction: (NoteEntity) -> Unit,
    onLeftAction: (NoteEntity) -> Unit,
    onRightButtonClick: (NoteEntity) -> Unit,
    onLeftButtonClick: (NoteEntity) -> Unit,
    actionSettings: NoteActionSettings
) {
    val canSwipeLeft = actionSettings.leftAction != NotesActions.NONE
    val canSwipeRight = actionSettings.rightAction != NotesActions.NONE
    val maxSwipePx = 80f

    var swipeOffset by remember { mutableFloatStateOf(0f) }
    var swipeState by remember { mutableStateOf(SwipeState.Default) }

    val draggableState = rememberDraggableState { delta ->
        // Allow only valid directions
        val newOffset = swipeOffset + delta
        swipeOffset = when {
            newOffset < 0 && canSwipeLeft -> newOffset.coerceIn(-maxSwipePx, 0f)
            newOffset > 0 && canSwipeRight -> newOffset.coerceIn(0f, maxSwipePx)
            else -> swipeOffset // block swipe if no action
        }

        // Update state continuously
        swipeState = when {
            swipeOffset >= maxSwipePx - 10 -> SwipeState.RightAction
            swipeOffset <= -maxSwipePx + 10 -> SwipeState.LeftAction
            else -> SwipeState.Default
        }
    }


    val selectionOffset by animateDpAsState(
        targetValue = if (selected) 40.dp else 0.dp,
        label = "selectionOffset"
    )

    // disable dragging when select mode is active
    val dragModifier = if (!isSelectMode && !isReorderMode) {
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
    } else Modifier

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .then(dragModifier)
    ) {
        // Swipe background
        val modifier = when {
            swipeOffset > 0f -> {
                Modifier
                    .width((swipeOffset + 50).dp)
                    .fillMaxHeight()
                    .align(Alignment.CenterStart)
                    .background(noteActionColor(actionSettings.rightAction))
            }
            swipeOffset < 0f -> {
                Modifier
                    .width((-swipeOffset + 50).dp)
                    .fillMaxHeight()
                    .align(Alignment.CenterEnd)
                    .background(noteActionColor(actionSettings.leftAction))
            }
            else -> Modifier
        }

        // Swipe background
        Box(modifier = modifier)

        // Swipe action icon
        if (swipeOffset != 0f) {
            val isActionReady = swipeState != SwipeState.Default
            Icon(
                imageVector = when {
                    swipeOffset > 0f -> noteActionIcon(actionSettings.rightAction)
                    swipeOffset < 0f -> noteActionIcon(actionSettings.leftAction)
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
                    .clickable { onNoteClick?.invoke(note) }
                    .padding(start = 12.dp)
                    .size(26.dp)
            )
        }

        // Foreground Note Card
        NoteCard(
            note = note,
            isSelectMode = isSelectMode,
            isReorderMode = isReorderMode,
            scale = scale,
            elevation = elevation,
            isDragging = isDragging,
            reorderState = reorderState,
            leftAction = leftAction,
            rightAction = rightAction,
            onClick = if (onNoteClick != null ) {
                { onNoteClick(note) }
            } else null,
            onLongClick = if (onNoteLongClick != null ) {
                { onNoteLongClick(note) }
            } else null,
            onRightButtonClick = { onRightButtonClick(note) },
            onLeftButtonClick = { onLeftButtonClick(note) },
            modifier = Modifier.offset(x = swipeOffset.dp + selectionOffset)
        )
    }
}