package org.elnix.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import org.elnix.notes.data.NoteEntity
import org.elnix.notes.data.settings.SwipeActionSettings
import org.elnix.notes.data.settings.swipeActionColor
import org.elnix.notes.data.settings.swipeActionIcon
import org.elnix.notes.ui.NoteViewModel
import org.elnix.notes.ui.helpers.NoteCard


@Composable
fun NotesList(
    notes: List<NoteEntity>,
    vm: NoteViewModel,
    navController: NavHostController,
    actionSettings: SwipeActionSettings
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(notes) { note ->
            SwipeableNoteCard(
                note = note,
                vm = vm,
                navController = navController,
                actionSettings = actionSettings
            )
        }
    }
}

@Composable
fun SwipeableNoteCard(
    note: NoteEntity,
    vm: NoteViewModel,
    navController: androidx.navigation.NavHostController,
    actionSettings: SwipeActionSettings
) {
    val scope = rememberCoroutineScope()

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

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(Color.Transparent)
            .draggable(
                state = draggableState,
                orientation = Orientation.Horizontal,
                onDragStopped = {
                    if (swipeState == SwipeState.RightAction) {
                        performAction(actionSettings.rightAction, vm, navController, note, scope)
                    } else if (swipeState == SwipeState.LeftAction) {
                        performAction(actionSettings.leftAction, vm, navController, note, scope)
                    }

                    // Instantly reset card after release
                    swipeOffset = 0f
                    swipeState = SwipeState.Default
                }
            )
    ) {
        // Background visible while dragging
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

        // Action icon on respective side
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

        // Foreground card (moves with swipe)
        NoteCard(
            note = note,
            onClick = {
                performAction(actionSettings.clickAction, vm, navController, note, scope)
            },
            onDeleteButtonClick = {
                scope.launch {
                    vm.delete(note)
                }
            },
            modifier = Modifier.offset(x = swipeOffset.dp)
        )
    }
}