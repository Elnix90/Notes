package org.elnix.notes

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.elnix.notes.data.NoteEntity
import org.elnix.notes.data.helpers.ClickType
import org.elnix.notes.data.helpers.GlobalNotesActions
import org.elnix.notes.data.helpers.NoteActionSettings
import org.elnix.notes.data.helpers.NoteViewType
import org.elnix.notes.data.helpers.NotesActions
import org.elnix.notes.data.helpers.TagItem
import org.elnix.notes.data.helpers.ToolBars
import org.elnix.notes.data.settings.stores.ActionSettingsStore
import org.elnix.notes.data.settings.stores.TagsSettingsStore
import org.elnix.notes.data.settings.stores.ToolbarsSettingsStore
import org.elnix.notes.data.settings.stores.UiSettingsStore
import org.elnix.notes.data.settings.stores.UserConfirmSettingsStore
import org.elnix.notes.ui.NoteViewModel
import org.elnix.notes.ui.helpers.AddNoteFab
import org.elnix.notes.ui.helpers.SortSelectorDialog
import org.elnix.notes.ui.helpers.UserValidation
import org.elnix.notes.ui.helpers.tags.TagEditorDialog
import org.elnix.notes.ui.helpers.toolbars.UnifiedToolbar
import org.elnix.notes.ui.theme.adjustBrightness

@Stable
enum class SwipeState { Default, LeftAction, RightAction }

@Composable
fun NotesScreen(vm: NoteViewModel, navController: NavHostController) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val notes by vm.notes.collectAsState()
    val actionSettings by ActionSettingsStore.getActionSettingsFlow(ctx).collectAsState(initial = NoteActionSettings())
    val showNotesNumber by UiSettingsStore.getShowNotesNumber(ctx).collectAsState(initial = true)
    val noteViewType by UiSettingsStore.getNoteViewType(ctx).collectAsState(initial = NoteViewType.LIST)
    val toolbars by ToolbarsSettingsStore.getToolbarsFlow(ctx).collectAsState(initial = ToolbarsSettingsStore.defaultList)
    val allTags by TagsSettingsStore.getTags(ctx).collectAsState(initial = emptyList())
    val enabledTagIds = allTags.filter { it.component4() }.map { it.id }.toSet()

    //Other settings got by settingsStores
    val showNoteDeleteConfirmation by UserConfirmSettingsStore.getShowUserValidationDeleteNote(ctx).collectAsState(initial = true)
    val showMultipleDeleteConfirmation by UserConfirmSettingsStore.getShowUserValidationMultipleDeleteNote(ctx).collectAsState(initial = true)

    var noteToDelete by remember { mutableStateOf<NoteEntity?>(null) }
    var showMultipleDeleteDialog by remember { mutableStateOf(false) }

    val toolbarsSpacing by ToolbarsSettingsStore.getToolbarsSpacing(ctx).collectAsState(initial = 8)


    // Manage selection state
    val isSelectingEnabled = toolbars.find { it.toolbar == ToolBars.SELECT }!!.enabled
    var selectedNotes by remember { mutableStateOf<Set<NoteEntity>>(emptySet()) }
    val isSeveralSelectedNotes = selectedNotes.size > 1
    var isMultiSelectMode by remember { mutableStateOf(false) }
    var isReorderMode by remember { mutableStateOf(false) }
    var showAddNoteMenu by remember { mutableStateOf(false) }

    // Search options
    var isSearchExpandedQuickActions by remember { mutableStateOf(false) }
    var isSearchExpandedTags by remember { mutableStateOf(false) }
    var isSearchExpandedSelect by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf<String?>(null) }

    // Sort
    var showSortDialog by remember { mutableStateOf(false) }


    // Which notes to show is dependent on tag selector
    val showTagSelector = toolbars.any { it.toolbar == ToolBars.TAGS && it.enabled }

    val notesToShow =
        if ( !showTagSelector || enabledTagIds.size == allTags.size ) notes
        else notes.filter { note -> note.tagIds.any { it in enabledTagIds } }

    val filteredNotes = remember(notesToShow, searchText) {
        if (searchText.isNullOrBlank()) notesToShow
        else {
            val query = searchText!!.trim().lowercase()
            notesToShow.filter { note ->
                note.title.lowercase().contains(query) ||
                note.desc.lowercase().contains(query) ||
                note.checklist.any { it.text.lowercase().contains(query) }
            }
        }
    }

    // Tags things
    var showTagDeleteConfirm by remember { mutableStateOf(false) }
    var editTag by remember { mutableStateOf<TagItem?>(null) }
    var showEditor by remember { mutableStateOf(false) }
    var showCreator by remember { mutableStateOf(false) }
    var initialTag by remember { mutableStateOf<TagItem?>(null) }


    // User actions
    fun onNoteLongClick(note: NoteEntity) {
        if (isSelectingEnabled){
            isMultiSelectMode = true
            selectedNotes = selectedNotes + note
        }
    }
    fun onNoteClick(note: NoteEntity) {
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
    fun performGroupDelete() {
        scope.launch {
            selectedNotes.forEach { note -> performAction(NotesActions.DELETE, vm, navController, note, scope) }
            selectedNotes = emptySet()
            isMultiSelectMode = false
        }
    }

    fun onGroupAction(action: NotesActions) {
        when(action) {
            NotesActions.DELETE -> {
                if (showMultipleDeleteConfirmation) {
                    showMultipleDeleteDialog = true
                } else {
                    performGroupDelete()
                }
            }
            else -> {
                scope.launch {
                    selectedNotes.forEach { note -> performAction(action, vm, navController, note, scope) }
                    selectedNotes = emptySet()
                    isMultiSelectMode = false
                }
            }
        }
    }

    if (showMultipleDeleteDialog) {
        UserValidation(
            title = stringResource(R.string.delete_multiple_notes),
            message = "${stringResource(R.string.are_you_sure_to_delete)} ${selectedNotes.size} notes ? ${stringResource(R.string.this_cant_be_undone)}!",
            onCancel = { showMultipleDeleteDialog = false },
            doNotRemindMeAgain = {
                scope.launch { UserConfirmSettingsStore.setShowUserValidationMultipleDeleteNote(ctx, false) }
            },
            onAgree = {
                showMultipleDeleteDialog = false
                performGroupDelete()
            }
        )
    }


    if (noteToDelete != null) {
        UserValidation(
            title = stringResource(R.string.delete_note),
            message = "${stringResource(R.string.are_you_sure_to_delete)} ${noteToDelete!!.title} ? ${stringResource(R.string.this_cant_be_undone)}!",
            onCancel = { noteToDelete = null },
            doNotRemindMeAgain = {
                scope.launch { UserConfirmSettingsStore.setShowUserValidationDeleteNote(ctx, false) }
            },
            onAgree = {
                scope.launch {
                    vm.delete(noteToDelete!!)
                    noteToDelete = null
                }
            }
        )
    }


    fun onGlobalToolbarAction(action: GlobalNotesActions, clickType: ClickType, tagItem: TagItem?, toolbar: ToolBars) {
        when (action) {
            GlobalNotesActions.ADD_NOTE -> showAddNoteMenu = true
            GlobalNotesActions.SEARCH -> when (toolbar) {
                ToolBars.SELECT -> isSearchExpandedSelect = !isSearchExpandedSelect
                ToolBars.TAGS -> isSearchExpandedTags = !isSearchExpandedTags
                ToolBars.QUICK_ACTIONS -> isSearchExpandedQuickActions = !isSearchExpandedQuickActions
                else ->  return
            }
            GlobalNotesActions.SORT -> showSortDialog = !showSortDialog
            GlobalNotesActions.SETTINGS -> navController.navigate(Routes.Settings.ROOT)
            GlobalNotesActions.DESELECT_ALL -> onGroupAction(NotesActions.SELECT)
            GlobalNotesActions.REORDER -> {
                Toast.makeText(
                    ctx,
                    ctx.getString(R.string.reorder_function_doesnt_work_yet),
                    Toast.LENGTH_SHORT
                ).show()
                /*isReorderMode = !isReorderMode*/ // TODO
            }
            GlobalNotesActions.EDIT_NOTE -> onGroupAction(NotesActions.EDIT)
            GlobalNotesActions.DELETE_NOTE -> onGroupAction(NotesActions.DELETE)
            GlobalNotesActions.COMPLETE_NOTE -> onGroupAction(NotesActions.COMPLETE)
            GlobalNotesActions.DUPLICATE_NOTE -> onGroupAction(NotesActions.DUPLICATE)
            GlobalNotesActions.TAG_FILTER -> when (clickType) {
                ClickType.NORMAL ->  scope.launch {
                    TagsSettingsStore.setAllTagsSelected(ctx, true)
                }
                ClickType.LONG -> scope.launch {
                    TagsSettingsStore.setAllTagsSelected(ctx, false)
                }
                else -> return
            }

            GlobalNotesActions.ADD_TAG ->  showCreator = true
            GlobalNotesActions.TAGS -> {
                val tag = tagItem!!
                when (clickType) {

                    ClickType.NORMAL -> scope.launch {
                        TagsSettingsStore.updateTag(ctx, tag.copy(selected = !tag.selected))
                    }

                    ClickType.LONG -> {
                        editTag = tag
                        showEditor = true
                    }

                    ClickType.DOUBLE -> {
                        editTag = tag
                        showTagDeleteConfirm = true
                    }
                }
            }
            GlobalNotesActions.SPACER1,
            GlobalNotesActions.SPACER2,
            GlobalNotesActions.SPACER3 -> return
        }
    }

    fun clearSearchFilters() {
        searchText = ""
        isSearchExpandedTags = false
        isSearchExpandedSelect = false
        isSearchExpandedQuickActions = false
    }

    LaunchedEffect(Unit) { vm.deleteAllEmptyNotes() }

    BackHandler {
        when {
            showAddNoteMenu -> showAddNoteMenu = false
            isMultiSelectMode -> {
                selectedNotes = emptySet()
                isMultiSelectMode = false
            }
            isReorderMode -> isReorderMode = false
            isSearchExpandedQuickActions || isSearchExpandedTags || isSearchExpandedSelect -> clearSearchFilters()
            else -> navController.popBackStack()
        }
    }

    // ----- DYNAMIC TOOLBARS POSITIONING -----
    val topBars = mutableListOf<@Composable () -> Unit>()
    val bottomBars = mutableListOf<@Composable () -> Unit>()
    var reachedSeparator = false

    toolbars.filter { it.enabled }.forEach { bar ->
        if (bar.toolbar == ToolBars.SEPARATOR) {
            reachedSeparator = true
        }
        val toolbarComposable: (@Composable () -> Unit)? = when (bar.toolbar) {
            ToolBars.SELECT -> if (isMultiSelectMode) {
                {
                    UnifiedToolbar(
                        ctx,
                        toolbar = ToolBars.SELECT,
                        scrollState = rememberScrollState(),
                        isMultiSelect = isSeveralSelectedNotes,
                        isSearchExpanded = isSearchExpandedSelect,
                        color = bar.color,
                        borderColor = bar.borderColor,
                        borderWidth = bar.borderWidth,
                        borderRadius = bar.borderRadius,
                        elevation = bar.elevation,
                        paddingLeft = bar.leftPadding,
                        paddingRight = bar.rightPadding,
                        onSearchChange = { if ( it.isNotBlank()) searchText = it }
                    ) { action, clickType, tagItem, toolbar -> onGlobalToolbarAction(action, clickType, tagItem, toolbar) }
                }
            }  else null

            ToolBars.TAGS -> {
                {
                    UnifiedToolbar(
                        ctx = ctx,
                        toolbar = ToolBars.TAGS,
                        scrollState = rememberScrollState(),
                        isMultiSelect = isSeveralSelectedNotes,
                        isSearchExpanded = isSearchExpandedTags,
                        color = bar.color,
                        borderColor = bar.borderColor,
                        borderWidth = bar.borderWidth,
                        borderRadius = bar.borderRadius,
                        elevation = bar.elevation,
                        paddingLeft = bar.leftPadding,
                        paddingRight = bar.rightPadding,
                        onSearchChange = { searchText = it }
                    ) { action, clickType, tagItem, toolbar -> onGlobalToolbarAction(action, clickType, tagItem, toolbar) }
                }
            }

            ToolBars.QUICK_ACTIONS -> {
                {
                    UnifiedToolbar(
                        ctx = ctx,
                        toolbar = ToolBars.QUICK_ACTIONS,
                        scrollState = rememberScrollState(),
                        isMultiSelect = isSeveralSelectedNotes,
                        isSearchExpanded = isSearchExpandedQuickActions,
                        color = bar.color,
                        borderColor = bar.borderColor,borderWidth = bar.borderWidth,
                        borderRadius = bar.borderRadius,
                        elevation = bar.elevation,
                        paddingLeft = bar.leftPadding,
                        paddingRight = bar.rightPadding,
                        onSearchChange = { searchText = it }
                    ) { action, clickType, tagItem, toolbar -> onGlobalToolbarAction(action, clickType, tagItem, toolbar) }
                }
            }
            ToolBars.SEPARATOR -> null
        }
        if (toolbarComposable != null) {
            if (reachedSeparator) bottomBars.add(toolbarComposable)
            else topBars.add(toolbarComposable)
        }
    }

    val topBarHeight = ((85 * topBars.size) + toolbarsSpacing * maxOf(0, topBars.size - 1)).dp
    val bottomBarHeight = ((85 * bottomBars.size) + toolbarsSpacing * maxOf(0, bottomBars.size - 1)).dp


    var notesNumberText: String? = null
    if (showNotesNumber) {
        notesNumberText = "${stringResource(R.string.note_number)} : ${notes.size}"
        if (notesToShow.size != notes.size) {
            notesNumberText += " • ${stringResource(R.string.filtered_bote_number)} : ${notesToShow.size}"
        }
    }

//    var isDragging by remember { mutableStateOf(false) }
//    val localNotes = remember { mutableStateListOf<NoteEntity>() }
//
//    val reorderState = rememberReorderableLazyListState(
//        onMove = { from, to ->
//            isDragging = true
//            val item = localNotes.removeAt(from.index)
//            localNotes.add(to.index, item)
//        },
//        onDragEnd = { _, _ ->
//            isDragging = false
//            scope.launch {
//                localNotes.forEachIndexed { index, note ->
//                    if (note.orderIndex != index)
//                        vm.update(note.copy(orderIndex = index))
//                }
//            }
//        }
//    )

//    LaunchedEffect(notes) {
//        if (!isDragging) {
//            val dbIds = notes.map { it.id }
//            val localIds = localNotes.map { it.id }
//            if (dbIds != localIds) {
//                localNotes.clear()
//                localNotes.addAll(notes)
//            }
//        }
//    }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(WindowInsets.systemBars.asPaddingValues())
            .imePadding()
    ) {
        if (notes.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.no_notes_yet),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.adjustBrightness(0.5f),
                    textAlign = TextAlign.Center
                )
            }
        } else if (notesToShow.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    Text(
                        text = stringResource(R.string.no_notes_show_due_to_filters),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.adjustBrightness(0.7f),
                    )

                    Text(
                        text = stringResource(R.string.reset_filters),
                        style = MaterialTheme.typography.bodyLarge.copy(textDecoration = TextDecoration.Underline),
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.clickable { scope.launch { TagsSettingsStore.setAllTagsSelected(ctx, true) } }
                    )
                }
            }
        } else if (filteredNotes.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    Text(
                        text = stringResource(R.string.search_not_found),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.adjustBrightness(0.7f),
                    )

                    Text(
                        text = stringResource(R.string.clear_search),
                        style = MaterialTheme.typography.bodyLarge.copy(textDecoration = TextDecoration.Underline),
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.clickable { clearSearchFilters() }
                    )
                }
            }

        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                when (noteViewType) {
                    NoteViewType.LIST -> NotesList(
                        notes = filteredNotes,
                        notesNumberText = notesNumberText,
                        selectedNotes = selectedNotes,
                        isSelectMode = isMultiSelectMode,
                        isReorderMode = isReorderMode,
                        topBarsHeight = topBarHeight,
                        bottomBarsHeight = bottomBarHeight,
                        onNoteClick = if (actionSettings.clickAction != NotesActions.NONE) ::onNoteClick else null,
                        onNoteLongClick = if (actionSettings.longClickAction != NotesActions.NONE) ::onNoteLongClick else null,
                        onRightAction = { note ->
                            if (actionSettings.rightAction == NotesActions.DELETE && showNoteDeleteConfirmation) noteToDelete = note
                            else performAction(actionSettings.rightAction, vm, navController, note, scope)
                        },
                        onLeftAction = { note ->
                            if (actionSettings.leftAction == NotesActions.DELETE && showNoteDeleteConfirmation) noteToDelete = note
                            else performAction(actionSettings.leftAction, vm, navController, note, scope)
                        },
                        onDeleteButtonClick = { note ->
                            if (actionSettings.rightButtonAction == NotesActions.DELETE && showNoteDeleteConfirmation) noteToDelete = note
                            else performAction(actionSettings.rightButtonAction, vm, navController, note, scope)
                        },
                        onTypeButtonClick = { note ->
                            if (actionSettings.leftButtonAction == NotesActions.DELETE && showNoteDeleteConfirmation) noteToDelete = note
                            else performAction(actionSettings.leftButtonAction, vm, navController, note, scope)
                        },
                        onOrderChanged = { newList -> vm.reorderNotes(newList) },
                        actionSettings = actionSettings
                    )
                    NoteViewType.GRID -> NotesGrid(
                        notes = notesToShow,
                        selectedNotes = selectedNotes,
                        onNoteClick = ::onNoteClick,
                        onNoteLongClick = ::onNoteLongClick
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .height(topBarHeight)
                .align(Alignment.TopCenter)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(toolbarsSpacing.dp)
        ) {
            topBars.forEach { it() }
        }

        Column(
            modifier = Modifier
                .height(bottomBarHeight)
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(toolbarsSpacing.dp)
        ) {
            bottomBars.forEach { it() }
        }

        if (showAddNoteMenu) {
            AddNoteFab(
                navController = navController,
                toolbarsOnBottom = bottomBars.size,
                onDismiss = { showAddNoteMenu = false }
            )
        }
    }

    // --- Tags Dialogs ---
    if (showEditor) {
        TagEditorDialog(
            initialTag = initialTag,
            scope = scope,
            onDismiss = { showEditor = false }
        )
    }

    if (showCreator) {
        TagEditorDialog(
            initialTag = null,
            scope = scope,
            onDismiss = { showCreator = false }
        )
    }

    if (showSortDialog) {
        SortSelectorDialog { showSortDialog = false }

    }

    if (showTagDeleteConfirm && editTag != null) {
        val tagToDelete = editTag!!
        UserValidation(
            title = stringResource(R.string.delete_tag),
            message = "${stringResource(R.string.tag_deletion_confirm)} '${tagToDelete.name}'?",
            onCancel = { showTagDeleteConfirm = false },
            onAgree = {
                showTagDeleteConfirm = false
                scope.launch { TagsSettingsStore.deleteTag(ctx, tagToDelete) }
            }
        )
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
        NotesActions.DUPLICATE -> scope.launch { navController.navigate("edit/${vm.duplicateNote(note.id)}?type=${note.type.name}") }
        NotesActions.NONE -> return
    }
}
