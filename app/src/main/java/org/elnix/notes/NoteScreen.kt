package org.elnix.notes

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.notes.data.Action
import org.elnix.notes.data.ActionSettings
import org.elnix.notes.data.NoteEntity
import org.elnix.notes.data.SettingsStore
import org.elnix.notes.ui.NoteViewModel
import java.text.SimpleDateFormat
import java.util.Locale


@Stable
enum class SwipeState { Default, LeftAction, RightAction }

@Composable
fun NotesScreen(vm: NoteViewModel, navController: androidx.navigation.NavHostController) {
    val notes by vm.notes.collectAsState()
    val ctx = LocalContext.current

    val actionSettings by SettingsStore.getActionSettingsFlow(ctx).collectAsState(
        initial = ActionSettings()
    )

    LaunchedEffect(Unit) {
        vm.deleteAllEmptyNotes()
    }

    if (notes.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No notes yet.\nTap + to create one!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )
        }
    } else {
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
}

@Composable
fun SwipeableNoteCard(
    note: NoteEntity,
    vm: NoteViewModel,
    navController: androidx.navigation.NavHostController,
    actionSettings: ActionSettings
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val maxSwipePx = 100f
    var swipeOffset by remember { mutableFloatStateOf(0f) }
    var swipeState by remember { mutableStateOf(SwipeState.Default) }

    val draggableState = rememberDraggableState { delta ->
        swipeOffset = (swipeOffset + delta).coerceIn(-maxSwipePx, maxSwipePx)
        swipeState = when {
            swipeOffset >= maxSwipePx -> SwipeState.RightAction
            swipeOffset <= -maxSwipePx -> SwipeState.LeftAction
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
                        swipeOffset > 0f -> actionColor(actionSettings.rightAction)
                        swipeOffset < 0f -> actionColor(actionSettings.leftAction)
                        else -> Color.Transparent
                    },
                    shape = RoundedCornerShape(12.dp)
                )
        )

        // Action icon on respective side
        if (swipeOffset != 0f) {
            Icon(
                imageVector = when {
                    swipeOffset > 0f -> actionIcon(actionSettings.rightAction)
                    swipeOffset < 0f -> actionIcon(actionSettings.leftAction)
                    else -> Icons.Default.Delete
                },
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .align(if (swipeOffset > 0f) Alignment.CenterStart else Alignment.CenterEnd)
                    .padding(horizontal = 24.dp)
            )
        }

        // Foreground card (moves with swipe)
        NoteCard(
            note = note,
            onClick = {
                performAction(actionSettings.clickAction, vm, navController, note, scope)
            },
            onDelete = {
                vm.delete(note)
                Toast.makeText(ctx, "Deleted", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.offset(x = swipeOffset.dp)
        )
    }
}


private fun performAction(
    action: Action,
    vm: NoteViewModel,
    navController: androidx.navigation.NavHostController,
    note: NoteEntity,
//    ctx: android.content.Context,
    scope: kotlinx.coroutines.CoroutineScope
) {
    when (action) {
        Action.DELETE -> {
            scope.launch {
                vm.delete(note)
            }
        }

        Action.COMPLETE -> {
            scope.launch {
                val isCompleted = note.isCompleted
                if (isCompleted) vm.markUnCompleted(note)
                else vm.markCompleted(note)
            }
        }

        Action.EDIT -> navController.navigate("edit/${note.id}")
    }
}

@Composable
fun NoteCard(note: NoteEntity, onClick: () -> Unit, onDelete: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(note.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun actionColor(action: Action): Color = when (action) {
    Action.DELETE -> MaterialTheme.colorScheme.error
    Action.COMPLETE -> MaterialTheme.colorScheme.primary
    Action.EDIT -> MaterialTheme.colorScheme.secondary
}

@Composable
private fun actionIcon(action: Action) = when (action) {
    Action.DELETE -> Icons.Default.Delete
    Action.EDIT -> Icons.Default.Edit
    Action.COMPLETE -> Icons.Default.CheckBox
}
