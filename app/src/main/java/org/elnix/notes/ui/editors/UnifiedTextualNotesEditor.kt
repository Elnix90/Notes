package org.elnix.notes.ui.editors

import android.content.ClipData
import android.content.ClipboardManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.elnix.notes.R
import org.elnix.notes.Routes
import org.elnix.notes.data.ChecklistItem
import org.elnix.notes.data.NoteEntity
import org.elnix.notes.data.helpers.NoteType
import org.elnix.notes.data.settings.stores.DebugSettingsStore
import org.elnix.notes.data.settings.stores.TagsSettingsStore
import org.elnix.notes.data.settings.stores.UiSettingsStore
import org.elnix.notes.data.settings.stores.UserConfirmEntry
import org.elnix.notes.data.settings.stores.UserConfirmSettingsStore
import org.elnix.notes.ui.NoteViewModel
import org.elnix.notes.ui.helpers.ExpandableSection
import org.elnix.notes.ui.helpers.QuickActionSection
import org.elnix.notes.ui.helpers.TextDivider
import org.elnix.notes.ui.helpers.UserValidation
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
fun UnifiedTextualNotesEditor(
    vm: NoteViewModel,
    activity: FragmentActivity,
    navController: NavController,
    noteId: Long?,
    noteType: NoteType,
    onExit: () -> Unit,
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val showNoteIdInEditor by DebugSettingsStore.getShowNoteIdInEditor(ctx).collectAsState(initial = false)

    // --- State ---
    var note by remember { mutableStateOf<NoteEntity?>(null) }
    var title by rememberSaveable { mutableStateOf("") }
    var desc by rememberSaveable { mutableStateOf("") }
    var createdNoteId by remember { mutableStateOf<Long?>(null) }
    var tagIds by remember { mutableStateOf<List<Long>>(emptyList()) }
    var hasExited by remember { mutableStateOf(false) }

    val checklist = remember { mutableStateListOf<ChecklistItem>() }
    var pseudoText by remember { mutableStateOf("") }

    // --- Initialize Note ---
    LaunchedEffect(noteId) {
        val n: NoteEntity = if (noteId != null) {
            vm.getById(noteId) ?: vm.getById(vm.addNoteAndReturnId(type = noteType))!!
        } else {
            val id = createdNoteId ?: vm.addNoteAndReturnId(type = noteType).also { createdNoteId = it }
            vm.getById(id)!!
        }

        note = n
        title = n.title
        desc = n.desc
        checklist.clear()
        checklist.addAll(n.checklist)
        tagIds = n.tagIds
        createdNoteId = n.id
    }

    val currentNote = note ?: return
    val allTags by TagsSettingsStore.getTags(ctx).collectAsState(initial = emptyList())
    val reminders by vm.remindersFor(currentNote.id).collectAsState(initial = emptyList())

    val showColorDropdownEditor by UiSettingsStore.getShowColorDropdownEditor(ctx).collectAsState(initial = false)
    val showReminderDropdownEditor by UiSettingsStore.getShowReminderDropdownEditor(ctx).collectAsState(initial = false)
    val showQuick by UiSettingsStore.getShowQuickActionsDropdownEditor(ctx).collectAsState(initial = false)
    val showTags by UiSettingsStore.getShowTagsDropdownEditor(ctx).collectAsState(initial = false)

    val showNoteDeleteConfirmation by UserConfirmSettingsStore.get(
        ctx = ctx,
        entry = UserConfirmEntry.SHOW_USER_VALIDATION_DELETE_NOTE
    ).collectAsState(initial = true)
    val showDeleteOffsetConfirmation by UserConfirmSettingsStore.get(
        ctx = ctx,
        entry = UserConfirmEntry.SHOW_USER_VALIDATION_DELETE_OFFSET
    ).collectAsState(initial = true)


    var noteToDelete by remember { mutableStateOf<NoteEntity?>(null) }

    val surfaceColor = MaterialTheme.colorScheme.surface

    // --- Exit Handling ---
    fun handleExit() {
        if (hasExited) return
        hasExited = true

        if (pseudoText.isNotBlank()) {
            checklist.add(ChecklistItem(pseudoText, false))
        }

        scope.launch {
            val n = withContext(Dispatchers.IO) { vm.getById(currentNote.id) } ?: return@launch
            val cleanedChecklist = checklist.map { it.copy(text = it.text.trim()) }.filter { it.text.isNotBlank() }

            if (title.isBlank() && desc.isBlank() && cleanedChecklist.isEmpty()) {
                withContext(Dispatchers.IO) { vm.delete(n) }
            } else {
                withContext(Dispatchers.IO) {
                    vm.update(n.copy(title = title.trim(), desc = desc.trim(), checklist = cleanedChecklist))
                }
            }

            withContext(Dispatchers.Main) {
                onExit()
            }
        }
    }

    DisposableEffect(Unit) { onDispose { handleExit() } }
    BackHandler { handleExit() }

    // --- UI ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(WindowInsets.systemBars.asPaddingValues())
            .padding(horizontal = 16.dp)
            .imePadding()
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(bottom = 150.dp)
        ) {
            // --- Title ---
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

            if (noteType == NoteType.CHECKLIST){
                item { TextDivider(stringResource(R.string.checklist)) }
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = surfaceColor,
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
                                            backgroundColor = surfaceColor,
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
                                        backgroundColor = surfaceColor
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
            } else {
                item {
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text(stringResource(R.string.description)) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            autoCorrectEnabled = true,
                            keyboardType = KeyboardType.Unspecified
                        ),
                        colors = AppObjectsColors.outlinedTextFieldColors()
                    )
                }
            }
            // --- Colors Section ---
            item {
                ExpandableSection(
                    title = stringResource(R.string.colors_text_literal),
                    expanded = showColorDropdownEditor,
                    onExpand = { scope.launch { UiSettingsStore.setShowColorDropdownEditor(ctx, it) } }
                ) {
                    NotesColorPickerSection(
                        currentNote,
                        onBgColorPicked = { colorInt -> scope.launch { updateNoteBgColor(currentNote.id, vm, Color(colorInt))?.let { note = it } } },
                        onTextColorPicked = { colorInt -> scope.launch { updateNoteTextColor(currentNote.id, vm, Color(colorInt))?.let { note = it } } },
                        onAutoSwitchToggle = { checked -> scope.launch { toggleAutoColor(currentNote.id, vm, checked, surfaceColor)?.let { note = it } } },
                        onRandomColorClick = { scope.launch { setRandomColor(currentNote.id, vm, currentNote.autoTextColor, surfaceColor)?.let { note = it } } }
                    )
                }
            }

            // --- Reminders Section ---
            item {
                ExpandableSection(
                    title = stringResource(R.string.reminders),
                    expanded = showReminderDropdownEditor,
                    horizontalAlignment = Alignment.Start,
                    onExpand = { scope.launch { UiSettingsStore.setShowReminderDropdownEditor(ctx, it) } }
                ) {
                    RemindersSection(
                        note = currentNote,
                        reminders = reminders,
                        activity = activity,
                        currentId = currentNote.id,
                        title = title,
                        vm = vm
                    )
                }
            }

            // --- Tags Section ---
            item {
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
                                scope.launch(Dispatchers.IO) { vm.update(currentNote.copy(tagIds = tagIds)) }
                            }
                        },
                        onRemoveTagFromNote = { tag ->
                            tagIds = tagIds.filterNot { it == tag.id }
                            scope.launch(Dispatchers.IO) { vm.update(currentNote.copy(tagIds = tagIds)) }
                        }
                    )
                }
            }

            // --- Quick Actions Section ---
            item {
                ExpandableSection(
                    title = stringResource(R.string.quick_actions),
                    expanded = showQuick,
                    horizontalAlignment = Alignment.Start,
                    onExpand = { scope.launch { UiSettingsStore.setShowQuickActionsDropdownEditor(ctx, it) } }
                ) {
                    QuickActionSection(
                        note = currentNote,
                        onComplete = { state -> note = note?.copy(isCompleted = state) },
                        onDuplicate = {
                            scope.launch { navController.navigate("edit/${vm.duplicateNote(currentNote.id)}?type=${currentNote.type.name}") }
                        },
                        onDelete = {
                            scope.launch {
                                if (showNoteDeleteConfirmation) noteToDelete = currentNote
                                else {
                                    vm.delete(currentNote)
                                    navController.navigate(Routes.NOTES)
                                }
                            }
                        }
                    )
                }
            }
            item {
                if (showNoteIdInEditor) {
                    Text(
                        text = "Note ID: ${noteId ?: " no ID"}",
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.clickable(enabled = noteId != null) {
                            noteId?.let {
                                val clipboard = ctx.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Note ID", it.toString())
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(ctx, "Id copied to clipboard", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }

        // --- Validate/Cancel Buttons ---
        Surface(color = MaterialTheme.colorScheme.background, tonalElevation = 3.dp) {
            ValidateCancelButtons(
                cancelContainerColor = MaterialTheme.colorScheme.background,
                onValidate = { handleExit() },
                onCancel = {
                    scope.launch {
                        val cleanedChecklist = checklist.map { it.text.trim() }.filter { it.isNotBlank() }
                        if (title.isBlank() && desc.isBlank() && cleanedChecklist.isEmpty()) vm.delete(currentNote)
                        onExit()
                    }
                }
            )
        }
    }

    // --- User Delete Validation ---
    if (noteToDelete != null) {
        UserValidation(
            title = stringResource(R.string.delete_note),
            message = "${stringResource(R.string.are_you_sure_to_delete)} ${noteToDelete!!.title}? ${stringResource(R.string.this_cant_be_undone)}",
            onCancel = { noteToDelete = null },
            doNotRemindMeAgain = { scope.launch {
                UserConfirmSettingsStore.set(ctx, UserConfirmEntry.SHOW_USER_VALIDATION_DELETE_NOTE, false)
            } },
            onAgree = {
                scope.launch {
                    vm.delete(noteToDelete!!)
                    noteToDelete = null
                    onExit()
                }
            }
        )
    }
}
