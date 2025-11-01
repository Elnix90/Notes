package org.elnix.notes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.elnix.notes.data.NoteEntity
import org.elnix.notes.data.helpers.NoteViewType
import org.elnix.notes.data.settings.NoteActionSettings
import org.elnix.notes.data.settings.NotesActions
import org.elnix.notes.data.settings.stores.ActionSettingsStore
import org.elnix.notes.data.settings.stores.UiSettingsStore
import org.elnix.notes.ui.NoteViewModel
import org.elnix.notes.ui.helpers.MultiSelectToolbar
import org.elnix.notes.ui.theme.adjustBrightness

@Stable
enum class SwipeState { Default, LeftAction, RightAction }

@Composable
fun NotesScreen(vm: NoteViewModel, navController: NavHostController) {
    val notes by vm.notes.collectAsState()
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedNotes by remember { mutableStateOf<Set<NoteEntity>>(emptySet()) }
    var isMultiSelectMode by remember { mutableStateOf(false) }

    val actionSettings by ActionSettingsStore.getActionSettingsFlow(ctx)
        .collectAsState(initial = NoteActionSettings())

    val showNotesNumber by UiSettingsStore.getShowNotesNumber(ctx)
        .collectAsState(initial = true)

    val noteViewType by UiSettingsStore.getNoteViewType(ctx)
        .collectAsState(initial = NoteViewType.LIST)

    val onNoteLongClick: (NoteEntity) -> Unit = { note ->
        isMultiSelectMode = true
        selectedNotes = selectedNotes + note
    }

    val onNoteClick: (NoteEntity) -> Unit = { note ->
        if (isMultiSelectMode) {
            selectedNotes = if (note in selectedNotes) {
                selectedNotes - note
            } else {
                selectedNotes + note
            }
            if (selectedNotes.isEmpty()) {
                isMultiSelectMode = false
            }
        } else {
            performAction(
                actionSettings.clickAction,
                vm, navController, note, scope,
                onSelectStart = { isMultiSelectMode = true; selectedNotes += note }
            )
        }
    }


    val onGroupAction: (NotesActions) -> Unit = { action ->
        scope.launch {
            selectedNotes.forEach { note ->
                performAction(action, vm, navController, note, scope)
            }
            selectedNotes = emptySet()
            isMultiSelectMode = false
        }
    }

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
                text = stringResource(R.string.no_notes_yet),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.adjustBrightness(0.5f),
                textAlign = TextAlign.Center
            )
        }
    } else {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (isMultiSelectMode) {
                MultiSelectToolbar(
                    onGroupAction = onGroupAction,
                    isSingleSelected = selectedNotes.size == 1,
                    onCloseSelection = {
                        selectedNotes = emptySet()
                        isMultiSelectMode = false
                    }
                )
            } else if (showNotesNumber) {
                Text(
                    text = "${stringResource(R.string.note_number)} : ${notes.size}",
                    color = MaterialTheme.colorScheme.onBackground.adjustBrightness(0.5f),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            }

            when (noteViewType) {
                NoteViewType.LIST -> NotesList(
                    notes = notes,
                    selectedNotes = selectedNotes,
                    isSelectMode = isMultiSelectMode,
                    onNoteClick = onNoteClick,
                    onNoteLongClick = onNoteLongClick,
                    onRightAction = { note ->
                        performAction(actionSettings.rightAction, vm, navController, note, scope)
                    },
                    onLeftAction = { note ->
                        performAction(actionSettings.leftAction, vm, navController, note, scope)
                    },
                    onButtonClick = { note ->
                        scope.launch { vm.delete(note) }
                    },
                    actionSettings = actionSettings
                )

                NoteViewType.GRID -> NotesGrid(
                    notes = notes,
                    selectedNotes = selectedNotes,
                    onNoteClick = onNoteClick,
                    onNoteLongClick = onNoteLongClick
                )
            }
        }
    }
}


fun performAction(
    action: NotesActions,
    vm: NoteViewModel,
    navController: NavHostController,
    note: NoteEntity,
    scope: CoroutineScope,
    onSelectStart: (() -> Unit)? = null
) {
    when (action) {
        NotesActions.DELETE -> {
            scope.launch { vm.delete(note) }
        }

        NotesActions.COMPLETE -> {
            scope.launch {
                if (note.isCompleted) vm.markUnCompleted(note)
                else vm.markCompleted(note)
            }
        }

        NotesActions.EDIT -> {
            navController.navigate("edit/${note.id}")
        }

        NotesActions.SELECT -> {
            onSelectStart?.invoke()
        }
    }
}
