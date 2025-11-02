package org.elnix.notes.ui.editors

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import org.elnix.notes.data.settings.stores.UiSettingsStore
import org.elnix.notes.ui.NoteViewModel
import org.elnix.notes.ui.helpers.CompletionToggle
import org.elnix.notes.ui.helpers.ExpandableSection
import org.elnix.notes.ui.helpers.RemindersSection
import org.elnix.notes.ui.helpers.ValidateCancelButtons
import org.elnix.notes.ui.helpers.colors.NotesColorPickerSection
import org.elnix.notes.ui.helpers.colors.setRandomColor
import org.elnix.notes.ui.helpers.colors.toggleAutoColor
import org.elnix.notes.ui.helpers.colors.updateNoteBgColor
import org.elnix.notes.ui.helpers.colors.updateNoteTextColor
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
    val scrollState = rememberScrollState()

    var note by remember { mutableStateOf<NoteEntity?>(null) }
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var createdNoteId by remember { mutableStateOf<Long?>(null) }
    var hasExited by rememberSaveable { mutableStateOf(false) }

    // Load or create note
    LaunchedEffect(noteId) {
        if (noteId != null) {
            vm.getById(noteId)?.let {
                note = it
                title = it.title
                desc = it.desc
            }
        } else if (createdNoteId == null) {
            val id = vm.addNoteAndReturnId(type = NoteType.TEXT)
            createdNoteId = id
            note = vm.getById(id)
        }
    }

    val currentId = note?.id ?: createdNoteId


    fun handleExit() {
        if (hasExited) return
        hasExited = true
        scope.launch {
            val id = note?.id ?: createdNoteId
            val result = withContext(Dispatchers.IO) {
                val n = id?.let { vm.getById(it) }
                when {
                    n == null -> "cancel"
                    title.isBlank() && desc.isBlank() -> {
                        vm.delete(n)
                        "cancel"
                    }
                    else -> {
                        vm.update(
                            n.copy(
                                title = title.trim(),
                                desc = desc.trim()
                            )
                        )
                        "saved"
                    }
                }
            }
            if (result == "saved") onSaved() else onCancel()
        }
    }

    // Call same handler on dispose and on back press
    DisposableEffect(Unit) {
        onDispose { handleExit() }
    }
    BackHandler { handleExit() }

    // --- UI ---

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(stringResource(R.string.title)) },
            modifier = Modifier.fillMaxWidth(),
            colors = AppObjectsColors.outlinedTextFieldColors()
        )

        OutlinedTextField(
            value = desc,
            onValueChange = { desc = it },
            label = { Text(stringResource(R.string.description)) },
            modifier = Modifier.fillMaxWidth(),
            colors = AppObjectsColors.outlinedTextFieldColors()
        )

        val reminders by remember(currentId) {
            if (currentId != null) vm.remindersFor(currentId) else null
        }?.collectAsState(initial = emptyList()) ?: remember { mutableStateOf(emptyList()) }



        val showColorDropdownEditor by UiSettingsStore.getShowColorDropdownEditor(ctx)
            .collectAsState(initial = false)

        ExpandableSection(
            title = stringResource(R.string.colors_text_literal),
            expanded = showColorDropdownEditor,
            onExpand = {
                scope.launch { UiSettingsStore.setShowColorDropdownEditor(ctx, it) }
            }
        ) {
            NotesColorPickerSection(
                note,
                scope,
                onBgColorPicked = { c ->
                    scope.launch {
                        updateNoteBgColor(currentId, vm, Color(c))?.let { note = it }
                    }
                },
                onTextColorPicked = { c ->
                    scope.launch {
                        updateNoteTextColor(currentId, vm, Color(c))?.let { note = it }
                    }
                },
                onAutoSwitchToggle = { checked ->
                    scope.launch { toggleAutoColor(currentId, vm, checked)?.let { note = it } }
                },
                onRandomColorClick = {
                    scope.launch { setRandomColor(currentId, vm)?.let { note = it } }
                }
            )
        }


        val showReminderDropdownEditor by UiSettingsStore.getShowReminderDropdownEditor(ctx)
            .collectAsState(initial = false)

        ExpandableSection(
            title = stringResource(R.string.reminders),
            expanded = showReminderDropdownEditor,
            horizontalAlignment = Alignment.Start,
            onExpand = {
                scope.launch { UiSettingsStore.setShowReminderDropdownEditor(ctx, it) }
            }
        ) {
            RemindersSection(reminders, currentId, title, vm)
        }


        val showQuickActionsDropdownEditor by UiSettingsStore.getShowQuickActionsDropdownEditor(ctx)
            .collectAsState(initial = false)

        ExpandableSection(
            title = stringResource(R.string.quick_actions),
            expanded = showQuickActionsDropdownEditor,
            onExpand = {
                scope.launch { UiSettingsStore.setShowQuickActionsDropdownEditor(ctx, it) }
            }
        ) {
            CompletionToggle(note, currentId, vm) { note = it }
        }

        Spacer(Modifier.height(15.dp))

        ValidateCancelButtons(
            onValidate = { handleExit() },
            onCancel = {
                scope.launch {
                    note?.let {
                        if (title.isBlank() && desc.isBlank()) vm.delete(it)
                    }
                    onCancel()
                }
            }
        )
    }
}
