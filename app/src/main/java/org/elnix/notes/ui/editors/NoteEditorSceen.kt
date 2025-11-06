package org.elnix.notes.ui.editors

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.elnix.notes.R
import org.elnix.notes.data.NoteEntity
import org.elnix.notes.data.helpers.NoteType
import org.elnix.notes.data.settings.stores.TagsSettingsStore
import org.elnix.notes.data.settings.stores.UiSettingsStore
import org.elnix.notes.ui.NoteViewModel
import org.elnix.notes.ui.helpers.CompletionToggle
import org.elnix.notes.ui.helpers.ExpandableSection
import org.elnix.notes.ui.helpers.ValidateCancelButtons
import org.elnix.notes.ui.helpers.colors.NotesColorPickerSection
import org.elnix.notes.ui.helpers.colors.setRandomColor
import org.elnix.notes.ui.helpers.colors.toggleAutoColor
import org.elnix.notes.ui.helpers.colors.updateNoteBgColor
import org.elnix.notes.ui.helpers.colors.updateNoteTextColor
import org.elnix.notes.ui.helpers.reminders.RemindersSection
import org.elnix.notes.ui.helpers.tags.TagsSection
import org.elnix.notes.ui.theme.AppObjectsColors

@Composable
fun NoteEditorScreen(
    vm: NoteViewModel,
    noteId: Long?,
    onSaved: () -> Unit,
    onCancel: () -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val scroll = rememberScrollState()

    // --- States ---
    var note by remember { mutableStateOf<NoteEntity?>(null) }
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var tagIds by remember { mutableStateOf<List<Long>>(emptyList()) }
    var hasExited by rememberSaveable { mutableStateOf(false) }

    // --- Load Note ---
    LaunchedEffect(noteId) {
        val n = withContext(Dispatchers.IO) {
            if (noteId != null) vm.getById(noteId) else null
        }

        val ensuredNote = n ?: run {
            val newId = vm.addNoteAndReturnId(type = NoteType.TEXT)
            withContext(Dispatchers.IO) { vm.getById(newId) }
        }

        ensuredNote?.let {
            note = it
            title = it.title
            desc = it.desc
            tagIds = it.tagIds
        }
    }

    val currentNote = note ?: return
    val allTags by TagsSettingsStore.getTags(ctx).collectAsState(initial = emptyList())

    // --- Save or Exit ---
    fun handleExit() {
        if (hasExited) return
        hasExited = true

        scope.launch {
            withContext(Dispatchers.IO) {
                if (title.isBlank() && desc.isBlank() && tagIds.isEmpty()) {
                    vm.delete(currentNote)
                } else {
                    vm.update(
                        currentNote.copy(
                            title = title.trim(),
                            desc = desc.trim(),
                            tagIds = tagIds
                        )
                    )
                }
            }
            if (title.isBlank() && desc.isBlank() && tagIds.isEmpty()) onCancel()
            else onSaved()
        }
    }

    DisposableEffect(Unit) { onDispose { handleExit() } }
    BackHandler { handleExit() }

    // --- Reminders ---
    val reminders by vm.remindersFor(currentNote.id).collectAsState(initial = emptyList())

    // --- UI ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scroll),
//            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(stringResource(R.string.title)) },
            modifier = Modifier.fillMaxWidth(),
            colors = AppObjectsColors.outlinedTextFieldColors()
        )

        // Description
        OutlinedTextField(
            value = desc,
            onValueChange = { desc = it },
            label = { Text(stringResource(R.string.description)) },
            modifier = Modifier.fillMaxWidth(),
            colors = AppObjectsColors.outlinedTextFieldColors()
        )

        // --- Colors ---
        val showColors by UiSettingsStore.getShowColorDropdownEditor(ctx)
            .collectAsState(initial = false)
        ExpandableSection(
            title = stringResource(R.string.colors_text_literal),
            expanded = showColors,
            onExpand = { scope.launch { UiSettingsStore.setShowColorDropdownEditor(ctx, it) } }
        ) {
            NotesColorPickerSection(
                note = currentNote,
                scope = scope,
                onBgColorPicked = { c ->
                    scope.launch { updateNoteBgColor(currentNote.id, vm, Color(c))?.let { note = it } }
                },
                onTextColorPicked = { c ->
                    scope.launch { updateNoteTextColor(currentNote.id, vm, Color(c))?.let { note = it } }
                },
                onAutoSwitchToggle = { checked ->
                    scope.launch { toggleAutoColor(currentNote.id, vm, checked)?.let { note = it } }
                },
                onRandomColorClick = {
                    scope.launch { setRandomColor(currentNote.id, vm, currentNote.autoTextColor)?.let { note = it } }
                }
            )
        }

        // --- Reminders ---
        val showReminders by UiSettingsStore.getShowReminderDropdownEditor(ctx)
            .collectAsState(initial = false)

        ExpandableSection(
            title = stringResource(R.string.reminders),
            expanded = showReminders,
            horizontalAlignment = Alignment.Start,
            onExpand = { scope.launch { UiSettingsStore.setShowReminderDropdownEditor(ctx, it) } }
        ) {
            RemindersSection(reminders, currentNote.id, title, vm)
        }

        // --- Tags ---
        val showTags by UiSettingsStore.getShowTagsDropdownEditor(ctx)
            .collectAsState(initial = false)

        ExpandableSection(
            title = stringResource(R.string.tags),
            expanded = showTags,
            horizontalAlignment = Alignment.Start,
            onExpand = { scope.launch { UiSettingsStore.setShowTagsDropdownEditor(ctx, it) } }
        ) {
            TagsSection(
                allTags = allTags,
                noteTagIds = tagIds,
                scope = scope,
                onAddTagToNote = { tag ->
                    if (!tagIds.contains(tag.id)) {
                        tagIds = tagIds + tag.id
                        scope.launch(Dispatchers.IO) {
                            vm.update(currentNote.copy(tagIds = tagIds))
                        }
                    }
                },
                onRemoveTagFromNote = { tag ->
                    tagIds = tagIds.filterNot { it == tag.id }
                    scope.launch(Dispatchers.IO) {
                        vm.update(currentNote.copy(tagIds = tagIds))
                    }
                }
            )
        }


        // --- Quick Actions ---
        val showQuick by UiSettingsStore.getShowQuickActionsDropdownEditor(ctx)
            .collectAsState(initial = false)
        ExpandableSection(
            title = stringResource(R.string.quick_actions),
            expanded = showQuick,
            horizontalAlignment = Alignment.Start,
            onExpand = { scope.launch { UiSettingsStore.setShowQuickActionsDropdownEditor(ctx, it) } }
        ) {
            CompletionToggle(currentNote, currentNote.id, vm) { note = it }
        }

        Spacer(Modifier.height(15.dp))

        ValidateCancelButtons(
            onValidate = { handleExit() },
            onCancel = {
                scope.launch {
                    if (title.isBlank() && desc.isBlank() && tagIds.isEmpty())
                        vm.delete(currentNote)
                    onCancel()
                }
            }
        )
    }
}
