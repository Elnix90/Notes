package org.elnix.notes

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.burnoutcrew.reorderable.ReorderableLazyListState
import org.elnix.notes.data.NoteEntity
import org.elnix.notes.data.helpers.NoteActionSettings
import org.elnix.notes.data.helpers.noteActionColor
import org.elnix.notes.data.helpers.noteActionIcon
import org.elnix.notes.ui.helpers.NoteCard

//@Composable
//fun NotesList(
//    notes: List<NoteEntity>,
//    selectedNotes: Set<NoteEntity>,
//    isSelectMode: Boolean,
//    isReorderMode: Boolean,
//    topBarsHeight: Dp,
//    bottomBarsHeight: Dp,
//    onNoteClick: (NoteEntity) -> Unit,
//    onNoteLongClick: (NoteEntity) -> Unit,
//    onRightAction: (NoteEntity) -> Unit,
//    onLeftAction: (NoteEntity) -> Unit,
//    onButtonClick: (NoteEntity) -> Unit,
//    onTypeButtonClick: (NoteEntity) -> Unit,
//    onOrderChanged: (List<NoteEntity>) -> Unit,
//    actionSettings: NoteActionSettings
//) {
//
//    val localList = remember { mutableStateListOf<NoteEntity>().apply { addAll(notes) } }
//    LaunchedEffect(notes) {
//        if (notes.map { it.id } != localList.map { it.id }) {
//            localList.clear()
//            localList.addAll(notes)
//        }
//    }
//
//    val reorderState = rememberReorderableLazyListState(
//        onMove = { from, to ->
//            localList.move(from.index, to.index)
//        },
//        onDragEnd = { _, _ ->
//            onOrderChanged(localList.toList())
//        }
//    )
//
//    LazyColumn(
//        state = reorderState.listState,
//        modifier = Modifier
//            .fillMaxSize()
//            .then(
//                if (isReorderMode)
//                    Modifier
//                        .reorderable(reorderState)
//                        .detectReorderAfterLongPress(reorderState)
//                else Modifier
//            ),
//        verticalArrangement = Arrangement.spacedBy(12.dp)
//    ) {
//        item { Box(Modifier.height(topBarsHeight)) }
//
//        items(localList.size, key = { localList[it].id }) { index ->
//            val note = localList[index]
//            ReorderableItem(state = reorderState, key = note.id) { isDragging ->
//                val scale by animateFloatAsState(if (isDragging) 1.03f else 1f)
//                val elevation by animateDpAsState(if (isDragging) 16.dp else 4.dp)
//                val bgColor =
//                    if (isDragging) note.bgColor.copy(alpha = 0.2f)
//                    else note.bgColor
//
//                SwipeableNoteCard(
//                    note = note,
//                    selected = selectedNotes.contains(note),
//                    isSelectMode = isSelectMode,
//                    isReorderMode = isReorderMode,
//                    reorderState = reorderState,
//                    scale = scale,
//                    elevation = elevation,
//                    bgColor = bgColor,
//                    isDragging = isDragging,
//                    onNoteClick = { onNoteClick(note) },
//                    onNoteLongClick = { onNoteLongClick(note) },
//                    onRightAction = { onRightAction(note) },
//                    onLeftAction = { onLeftAction(note) },
//                    onButtonClick = { onButtonClick(note) },
//                    onTypeButtonClick = { onTypeButtonClick(note) },
//                    actionSettings = actionSettings
//                )
//            }
//        }
//
//        item { Box(Modifier.height(bottomBarsHeight)) }
//    }
//}

@Composable
fun SwipeableNoteCard(
    note: NoteEntity,
    selected: Boolean,
    isSelectMode: Boolean,
    isReorderMode: Boolean,
    scale: Float,
    elevation: Dp,
    bgColor: Color,
    isDragging: Boolean,
    reorderState: ReorderableLazyListState,
    onNoteClick: (NoteEntity) -> Unit,
    onNoteLongClick: (NoteEntity) -> Unit,
    onRightAction: (NoteEntity) -> Unit,
    onLeftAction: (NoteEntity) -> Unit,
    onButtonClick: (NoteEntity) -> Unit,
    onTypeButtonClick: (NoteEntity) -> Unit,
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
                    .clickable { onNoteClick(note) }
                    .padding(start = 12.dp)
                    .size(26.dp)
            )
        }

        // Foreground Note Card
        NoteCard(
            note = note,
            isReorderMode = isReorderMode,
            scale = scale,
            elevation = elevation,
            bgColor = bgColor,
            isDragging = isDragging,
            reorderState = reorderState,
            onClick = { onNoteClick(note) },
            onLongClick = { onNoteLongClick(note) },
            onDeleteButtonClick = { onButtonClick(note) },
            onTypeIconClick = { onTypeButtonClick(note) },
            modifier = Modifier.offset(x = swipeOffset.dp + selectionOffset)
        )
    }
}


