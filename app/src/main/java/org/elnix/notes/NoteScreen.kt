package org.elnix.notes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import org.elnix.notes.data.helpers.NoteActionSettings
import org.elnix.notes.data.helpers.NoteViewType
import org.elnix.notes.data.helpers.NotesActions
import org.elnix.notes.data.settings.stores.ActionSettingsStore
import org.elnix.notes.data.settings.stores.TagsSettingsStore
import org.elnix.notes.data.settings.stores.UiSettingsStore
import org.elnix.notes.ui.NoteViewModel
import org.elnix.notes.ui.helpers.MultiSelectToolbar
import org.elnix.notes.ui.helpers.TextDivider
import org.elnix.notes.ui.helpers.tags.TagSelectingRow
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

    val showTagSelector by UiSettingsStore.getShowTagSelector(ctx).collectAsState(initial = false)
    val tagSelectorPositionBottom by UiSettingsStore.getTagSelectorPositionBottom(ctx).collectAsState(initial = false)
    val multiSelectToolbarPositionBottom by UiSettingsStore.getMultiSelectToolbarPositionBottom(ctx).collectAsState(initial = true)

    val allTags by TagsSettingsStore.getTags(ctx).collectAsState(initial = emptyList())
    val enabledTagIds = allTags.filter { it.component4() }.map { it.id }.toSet()

    val notesToShow = if (enabledTagIds.size == allTags.size || !showTagSelector) {
        notes
    } else {
        notes.filter { note -> note.tagIds.any { it in enabledTagIds } }
    }

    val onNoteLongClick: (NoteEntity) -> Unit = { note ->
        isMultiSelectMode = true
        selectedNotes = selectedNotes + note
    }

    val onNoteClick: (NoteEntity) -> Unit = { note ->
        if (isMultiSelectMode) {
            selectedNotes = if (note in selectedNotes) selectedNotes - note else selectedNotes + note
            if (selectedNotes.isEmpty()) isMultiSelectMode = false
        } else {
            performAction(
                actionSettings.clickAction, vm, navController, note, scope,
                onSelectStart = { isMultiSelectMode = true; selectedNotes += note }
            )
        }
    }

    val onGroupAction: (NotesActions) -> Unit = { action ->
        scope.launch {
            selectedNotes.forEach { note -> performAction(action, vm, navController, note, scope) }
            selectedNotes = emptySet()
            isMultiSelectMode = false
        }
    }

    LaunchedEffect(Unit) { vm.deleteAllEmptyNotes() }

    // --- Decide toolbar & selector positions ---
    val topBars = mutableListOf<@Composable () -> Unit>()
    val bottomBars = mutableListOf<@Composable () -> Unit>()

    if (showTagSelector) {
        val tagSelectorComposable: @Composable () -> Unit = {
            TagSelectingRow(ctx = ctx, allTags = allTags, scope = scope)
        }
        if (tagSelectorPositionBottom) bottomBars.add(tagSelectorComposable)
        else topBars.add(tagSelectorComposable)
    }

    if (isMultiSelectMode) {
        val multiSelectComposable: @Composable () -> Unit = {
            MultiSelectToolbar(
                onGroupAction = onGroupAction,
                isSingleSelected = selectedNotes.size == 1,
                onCloseSelection = {
                    selectedNotes = emptySet()
                    isMultiSelectMode = false
                }
            )
        }

        if (multiSelectToolbarPositionBottom) {
            if (showTagSelector && tagSelectorPositionBottom) {
                // both bottom: multi-select below selector
                bottomBars.add(multiSelectComposable)
            } else bottomBars.add(0, multiSelectComposable)
        } else {
            if (showTagSelector && !tagSelectorPositionBottom) {
                // both top: multi-select above selector
                topBars.add(0, multiSelectComposable)
            } else topBars.add(multiSelectComposable)
        }
    }

    Scaffold(
        topBar = {
            if(!topBars.isEmpty()) {
                Column { topBars.forEach { it() } }
            }
        },
        bottomBar = {
            if (!bottomBars.isEmpty()) {
                Column { bottomBars.forEach { it() } }
            }
        }
    ) { innerPadding ->
        if (notes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
                    .padding(innerPadding),
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (showNotesNumber) {
                    var text = "${stringResource(R.string.note_number)} : ${notes.size}"
                    if (notesToShow.size != notes.size) {
                        text += " • ${stringResource(R.string.filtered_bote_number)} : ${notesToShow.size}"
                    }
                    TextDivider(text)
                }

                when (noteViewType) {
                    NoteViewType.LIST -> NotesList(
                        notes = notesToShow,
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
                        onButtonClick = { note -> scope.launch { vm.delete(note) } },
                        onTypeButtonClick = { note ->
                            performAction(actionSettings.typeButtonAction, vm, navController, note, scope)
                        },
                        actionSettings = actionSettings
                    )

                    NoteViewType.GRID -> NotesGrid(
                        notes = notesToShow,
                        selectedNotes = selectedNotes,
                        onNoteClick = onNoteClick,
                        onNoteLongClick = onNoteLongClick
                    )
                }
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
        NotesActions.DELETE -> scope.launch { vm.delete(note) }
        NotesActions.COMPLETE -> scope.launch {
            if (note.isCompleted) vm.markUnCompleted(note)
            else vm.markCompleted(note)
        }
        NotesActions.EDIT -> navController.navigate("edit/${note.id}?type=${note.type.name}")
        NotesActions.SELECT -> onSelectStart?.invoke()
    }
}
