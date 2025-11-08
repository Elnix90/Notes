package org.elnix.notes

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import org.elnix.notes.data.NoteEntity
import org.elnix.notes.data.helpers.NoteActionSettings
import org.elnix.notes.ui.helpers.SwipeableNoteCard
import org.elnix.notes.ui.helpers.TextDivider


@Composable
fun NotesList(
    notes: List<NoteEntity>,
    notesNumberText: String?,
    selectedNotes: Set<NoteEntity>,
    isSelectMode: Boolean,
    isReorderMode: Boolean,
    topBarsHeight: Dp,
    bottomBarsHeight: Dp,
    onNoteClick: (NoteEntity) -> Unit,
    onNoteLongClick: (NoteEntity) -> Unit,
    onRightAction: (NoteEntity) -> Unit,
    onLeftAction: (NoteEntity) -> Unit,
    onButtonClick: (NoteEntity) -> Unit,
    onTypeButtonClick: (NoteEntity) -> Unit,
    onOrderChanged: (List<NoteEntity>) -> Unit,
    actionSettings: NoteActionSettings
) {

    var isDragging by remember { mutableStateOf(false) }
    val localNotes = remember { mutableStateListOf<NoteEntity>() }

    val reorderState = rememberReorderableLazyListState(
        onMove = { from, to ->
            isDragging = true
            val item = localNotes.removeAt(from.index)
            localNotes.add(to.index, item)
        },
        onDragEnd = { _, _ ->
            isDragging = false
            onOrderChanged(localNotes)
        }
    )

    LaunchedEffect(notes, isDragging) {
        if (!isDragging && notes.isNotEmpty()) {
            localNotes.clear()
            localNotes.addAll(notes)
        }
    }


    LazyColumn(
        state = reorderState.listState,
        modifier = Modifier
            .fillMaxSize()
            .then(
                if (isReorderMode)
                    Modifier
                        .reorderable(reorderState)
                        .detectReorderAfterLongPress(reorderState)
                else Modifier
            ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Dummy box to allow scroll until notes aren't under the floating toolbars
        item { Box(Modifier.height(topBarsHeight)) }

        // Show notes number under the pseudo box
        item { if (notesNumberText != null) {
            TextDivider(notesNumberText, Modifier.padding(horizontal = 16.dp))
        } }

        items(localNotes.size, key = { localNotes[it].id }) { index ->
            val note = localNotes[index]

            ReorderableItem(state = reorderState, key = note.id) { isDragging ->
                val scale by animateFloatAsState(if (isDragging) 1.03f else 1f)
                val elevation by animateDpAsState(if (isDragging) 16.dp else 4.dp)
                val bgColor =
                    if (isDragging) note.bgColor.copy(alpha = 0.2f)
                    else note.bgColor


                SwipeableNoteCard(
                    note = note,
                    selected = selectedNotes.contains(note),
                    isSelectMode = isSelectMode,
                    isReorderMode = isReorderMode,
                    reorderState = reorderState,
                    scale = scale,
                    elevation = elevation,
                    bgColor = bgColor,
                    isDragging = isDragging,
                    onNoteClick = { onNoteClick(note) },
                    onNoteLongClick = { onNoteLongClick(note) },
                    onRightAction = { onRightAction(note) },
                    onLeftAction = { onLeftAction(note) },
                    onButtonClick = { onButtonClick(note) },
                    onTypeButtonClick = { onTypeButtonClick(note) },
                    actionSettings = actionSettings
                )
            }
        }
        item { Box(Modifier.height(bottomBarsHeight)) }
    }
}
