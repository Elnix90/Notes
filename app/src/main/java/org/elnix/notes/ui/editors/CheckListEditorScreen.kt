package org.elnix.notes.ui.editors

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.elnix.notes.R
import org.elnix.notes.data.ChecklistItem
import org.elnix.notes.data.NoteEntity
import org.elnix.notes.data.helpers.NoteType
import org.elnix.notes.data.settings.stores.TagsSettingsStore
import org.elnix.notes.data.settings.stores.UiSettingsStore
import org.elnix.notes.ui.NoteViewModel
import org.elnix.notes.ui.helpers.CompletionToggle
import org.elnix.notes.ui.helpers.ExpandableSection
import org.elnix.notes.ui.helpers.TextDivider
import org.elnix.notes.ui.helpers.ValidateCancelButtons
import org.elnix.notes.ui.helpers.colors.NotesColorPickerSection
import org.elnix.notes.ui.helpers.colors.setRandomColor
import org.elnix.notes.ui.helpers.colors.toggleAutoColor
import org.elnix.notes.ui.helpers.colors.updateNoteBgColor
import org.elnix.notes.ui.helpers.colors.updateNoteTextColor
import org.elnix.notes.ui.helpers.reminders.RemindersSection
import org.elnix.notes.ui.helpers.tags.TagsSection
import org.elnix.notes.ui.theme.AppObjectsColors
import org.elnix.notes.ui.theme.adjustBrightness

@Composable
fun ChecklistEditorScreen(
    vm: NoteViewModel,
    noteId: Long?,
    onSaved: () -> Unit,
    onCancel: () -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    var note by remember { mutableStateOf<NoteEntity?>(null) }
    var title by rememberSaveable { mutableStateOf("") }
    var createdNoteId by remember { mutableStateOf<Long?>(null) }
    var tagIds by remember { mutableStateOf<List<Long>>(emptyList()) }
    var hasExited by remember { mutableStateOf(false) }

    val checklist = remember { mutableStateListOf<ChecklistItem>() }
    var pseudoText by remember { mutableStateOf("") }

    LaunchedEffect(noteId) {
        if (noteId != null) {
            vm.getById(noteId)?.let {
                note = it
                title = it.title
                checklist.clear()
                checklist.addAll(it.checklist)
                tagIds = it.tagIds
            }
        } else if (createdNoteId == null) {
            val id = vm.addNoteAndReturnId(type = NoteType.CHECKLIST)
            createdNoteId = id
            vm.getById(id)?.let {
                note = it
                title = it.title
                checklist.clear()
                checklist.addAll(it.checklist)
                tagIds = it.tagIds
            }
        }
    }

    val currentNote = note ?: return
    val allTags by TagsSettingsStore.getTags(ctx).collectAsState(initial = emptyList())

    fun handleExit() {
        if (hasExited) return
        hasExited = true

        if (pseudoText.isNotBlank()) {
            checklist.add(ChecklistItem(pseudoText, false))
        }

        scope.launch {
            val id = note?.id ?: createdNoteId ?: return@launch
            val n = withContext(Dispatchers.IO) { vm.getById(id) } ?: return@launch

            val cleanedChecklist = checklist.map { it.copy(text = it.text.trim()) }.filter { it.text.isNotBlank() }

            val result = when {
                title.isBlank() && cleanedChecklist.isEmpty() -> {
                    withContext(Dispatchers.IO) { vm.delete(n) }
                    "cancel"
                }
                else -> {
                    withContext(Dispatchers.IO) {
                        vm.update(n.copy(
                            title = title.trim(),
                            checklist = cleanedChecklist
                        ))
                    }
                    "saved"
                }
            }

            withContext(Dispatchers.Main) {
                if (result == "saved") onSaved() else onCancel()
            }
        }
    }

    DisposableEffect(Unit) { onDispose { handleExit() } }
    BackHandler { handleExit() }

    val reminders by vm.remindersFor(currentNote.id).collectAsState(initial = emptyList())

    val showColorDropdownEditor by UiSettingsStore.getShowColorDropdownEditor(ctx).collectAsState(initial = false)
    val showReminderDropdownEditor by UiSettingsStore.getShowReminderDropdownEditor(ctx).collectAsState(initial = false)
    val showQuick by UiSettingsStore.getShowQuickActionsDropdownEditor(ctx).collectAsState(initial = false)
    val showTags by UiSettingsStore.getShowTagsDropdownEditor(ctx).collectAsState(initial = false)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(WindowInsets.systemBars.asPaddingValues())
            .imePadding()
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 5.dp)
        ) {
            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.title)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        autoCorrectEnabled = true,
                        keyboardType = KeyboardType.Unspecified
                    ),
                    colors = AppObjectsColors.outlinedTextFieldColors()
                )
            }

            item { TextDivider(stringResource(R.string.checklist)) }

            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        checklist.forEachIndexed { index, item ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Checkbox(
                                    checked = item.checked,
                                    onCheckedChange = {
                                        checklist[index] = item.copy(checked = it)
                                    },
                                    colors = AppObjectsColors.checkboxColors()
                                )

                                TextField(
                                    value = item.text,
                                    onValueChange = { checklist[index] = item.copy(text = it) },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(
                                        capitalization = KeyboardCapitalization.Sentences,
                                        autoCorrectEnabled = true,
                                        keyboardType = KeyboardType.Unspecified
                                    ),
                                    colors = AppObjectsColors.outlinedTextFieldColors(
                                        backgroundColor = MaterialTheme.colorScheme.surface,
                                        onBackgroundColor = MaterialTheme.colorScheme.onSurface.adjustBrightness(
                                            if (item.checked) 0.5f else 1f
                                        )
                                    )
                                )

                                IconButton(onClick = { checklist.removeAt(index) }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = stringResource(R.string.remove)
                                    )
                                }
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextField(
                                value = pseudoText,
                                onValueChange = { pseudoText = it },
                                label = { Text(stringResource(R.string.new_entry)) },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Sentences,
                                    autoCorrectEnabled = true,
                                    keyboardType = KeyboardType.Unspecified,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        if (pseudoText.isNotBlank()) {
                                            checklist.add(ChecklistItem(pseudoText, false))
                                            pseudoText = ""
                                        }
                                    }
                                ),
                                colors = AppObjectsColors.outlinedTextFieldColors(
                                    backgroundColor = MaterialTheme.colorScheme.surface
                                )
                            )
                            IconButton(
                                onClick = {
                                    if (pseudoText.isNotBlank()) {
                                        checklist.add(ChecklistItem(pseudoText, false))
                                        pseudoText = ""
                                    }
                                },
                                enabled = pseudoText.isNotBlank()
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = stringResource(R.string.add_item)
                                )
                            }
                        }
                    }
                }
            }

            item {
                ExpandableSection(
                    title = stringResource(R.string.colors_text_literal),
                    expanded = showColorDropdownEditor,
                    onExpand = {
                        scope.launch {
                            UiSettingsStore.setShowColorDropdownEditor(
                                ctx,
                                it
                            )
                        }
                    }
                ) {
                    NotesColorPickerSection(
                        note,
                        scope,
                        onBgColorPicked = { colorInt ->
                            scope.launch {
                                updateNoteBgColor(
                                    currentNote.id,
                                    vm,
                                    Color(colorInt)
                                )?.let { note = it }
                            }
                        },
                        onTextColorPicked = { colorInt ->
                            scope.launch {
                                updateNoteTextColor(
                                    currentNote.id,
                                    vm,
                                    Color(colorInt)
                                )?.let { note = it }
                            }
                        },
                        onAutoSwitchToggle = { checked ->
                            scope.launch {
                                toggleAutoColor(
                                    currentNote.id,
                                    vm,
                                    checked
                                )?.let { note = it }
                            }
                        },
                        onRandomColorClick = {
                            scope.launch {
                                setRandomColor(
                                    currentNote.id,
                                    vm,
                                    note?.autoTextColor ?: true
                                )?.let { note = it }
                            }
                        }
                    )
                }
            }

            item {
                ExpandableSection(
                    title = stringResource(R.string.reminders),
                    expanded = showReminderDropdownEditor,
                    horizontalAlignment = Alignment.Start,
                    onExpand = {
                        scope.launch {
                            UiSettingsStore.setShowReminderDropdownEditor(
                                ctx,
                                it
                            )
                        }
                    }
                ) {
                    RemindersSection(reminders, currentNote.id, title, vm)
                }
            }

            item {
                // --- Tags ---


                ExpandableSection(
                    title = stringResource(R.string.tags),
                    expanded = showTags,
                    horizontalAlignment = Alignment.Start,
                    onExpand = {
                        scope.launch {
                            UiSettingsStore.setShowTagsDropdownEditor(
                                ctx,
                                it
                            )
                        }
                    }
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
            }

            item {
                // --- Quick Actions ---
                ExpandableSection(
                    title = stringResource(R.string.quick_actions),
                    expanded = showQuick,
                    horizontalAlignment = Alignment.Start,
                    onExpand = {
                        scope.launch {
                            UiSettingsStore.setShowQuickActionsDropdownEditor(
                                ctx,
                                it
                            )
                        }
                    }
                ) {
                    CompletionToggle(currentNote, currentNote.id, vm) { note = it }
                }
            }
        }



        Surface(
            color = MaterialTheme.colorScheme.background,
            tonalElevation = 3.dp
        ) {
            ValidateCancelButtons(
                onValidate = { handleExit() },
                onCancel = {
                    scope.launch {
                        val cleanedChecklist =
                            checklist.map { it.text.trim() }.filter { it.isNotBlank() }
                        if (title.isBlank() && cleanedChecklist.isEmpty()) {
                            note?.let { vm.delete(it) }
                        }
                        onCancel()
                    }
                }
            )
        }
    }
}

