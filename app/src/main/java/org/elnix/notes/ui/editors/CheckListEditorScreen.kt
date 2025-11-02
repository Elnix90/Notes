package org.elnix.notes.ui.editors

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.notes.R
import org.elnix.notes.data.ChecklistItem
import org.elnix.notes.data.NoteEntity
import org.elnix.notes.data.helpers.NoteType
import org.elnix.notes.ui.NoteViewModel
import org.elnix.notes.ui.helpers.RemindersSection
import org.elnix.notes.ui.helpers.TextDivider
import org.elnix.notes.ui.helpers.ValidateCancelButtons
import org.elnix.notes.ui.helpers.colors.NotesColorPickerSection
import org.elnix.notes.ui.helpers.colors.setRandomColor
import org.elnix.notes.ui.helpers.colors.toggleAutoColor
import org.elnix.notes.ui.helpers.colors.updateNoteBgColor
import org.elnix.notes.ui.helpers.colors.updateNoteTextColor
import org.elnix.notes.ui.theme.AppObjectsColors

@Composable
fun ChecklistEditorScreen(
    vm: NoteViewModel,
    noteId: Long?,
    onSaved: () -> Unit,
    onCancel: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var note by remember { mutableStateOf<NoteEntity?>(null) }
    var createdNoteId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(noteId) {
        if (noteId != null) {
            note = vm.getById(noteId)
        } else if (createdNoteId == null) {
            val id = vm.addNoteAndReturnId(type = NoteType.CHECKLIST)
            createdNoteId = id
            note = vm.getById(id)
        }
    }

    val currentId = note?.id ?: createdNoteId

    val reminderText = stringResource(R.string.reminder)

    val reminders by remember(currentId) {
        if (currentId != null) vm.remindersFor(currentId) else null
    }?.collectAsState(initial = emptyList()) ?: remember { mutableStateOf(emptyList()) }


    LazyColumn(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Title
        item {
            OutlinedTextField(
                value = note?.title ?: "",
                onValueChange = { t -> note = note?.copy(title = t) },
                label = { Text(stringResource(R.string.title)) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item { TextDivider(stringResource(R.string.checklist)) }

        // Checklist inside rounded surface
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
                    val list = note?.checklist ?: emptyList()
                    val realItems = list.filter { it.text.isNotEmpty() }
                    var pseudoText by remember { mutableStateOf("") }


                    realItems.forEachIndexed { i, item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = item.checked,
                                onCheckedChange = { checked ->
                                    note = note?.copy(
                                        checklist = list.toMutableList().apply {
                                            val index = indexOf(item)
                                            if(index >= 0) this[index] = item.copy(checked = checked)
                                        }
                                    )
                                },
                                colors = AppObjectsColors.checkboxColors()
                            )

                            TextField(
                                value = item.text,
                                onValueChange = { txt ->
                                    note = note?.copy(
                                        checklist = list.toMutableList().apply {
                                            val index = indexOf(item)
                                            if(index >= 0) this[index] = item.copy(text = txt)
                                        }
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                colors = AppObjectsColors.outlinedTextFieldColors(
                                    backgroundColor = MaterialTheme.colorScheme.surface,
                                    onBackgroundColor = MaterialTheme.colorScheme.onSurface
                                )
                            )

                            IconButton(onClick = {
                                note = note?.copy(
                                    checklist = list.toMutableList().apply { remove(item) }
                                )
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.remove))
                            }
                        }
                    }


                    // Shared function inside your Composable
                    fun addPseudoItem() {
                        if (pseudoText.isNotBlank()) {
                            note = note?.copy(
                                checklist = (note?.checklist ?: emptyList()).toMutableList().apply {
                                    add(ChecklistItem(pseudoText, false))
                                }
                            )
                            pseudoText = "" // reset for next entry
                        }
                    }


                    // Pseudo item
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        TextField(
                            value = pseudoText,
                            onValueChange = { txt -> pseudoText = txt },
                            label = { Text(stringResource(R.string.new_entry)) },
                            modifier = Modifier.weight(1f),
                            colors = AppObjectsColors.outlinedTextFieldColors(
                                backgroundColor = MaterialTheme.colorScheme.surface,
                                onBackgroundColor = MaterialTheme.colorScheme.onSurface
                            ),
                            enabled = true,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    addPseudoItem()
                                }
                            )
                        )

                        IconButton(
                            onClick = { addPseudoItem() },
                            enabled = pseudoText.isNotBlank()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_item))
                        }
                    }
                }
            }
        }

        item { TextDivider(stringResource(R.string.color_text_literal)) }

        item {
            NotesColorPickerSection(
                note = note,
                scope = scope,
                onBgColorPicked = { pickedInt ->
                    val pickedColor = Color(pickedInt)
                    scope.launch {
                        val updated = updateNoteBgColor(currentId, vm, pickedColor)
                        if (updated != null) note = updated
                    }
                },
                onTextColorPicked = { pickedInt ->
                    val pickedColor = Color(pickedInt)
                    scope.launch {
                        val updated = updateNoteTextColor(currentId, vm, pickedColor)
                        if (updated != null) note = updated
                    }
                },
                onAutoSwitchToggle = { checked ->
                    scope.launch {
                        val updated = toggleAutoColor(currentId, vm, checked)
                        if (updated != null) note = updated
                    }
                },
                onRandomColorClick = {
                    scope.launch {
                        val updated = setRandomColor(currentId, vm)
                        if (updated != null) note = updated
                    }
                }
            )
        }


        item {
            TextDivider(reminderText)

            RemindersSection(
                reminders = reminders,
                currentId = currentId,
                title = note?.title ?: stringResource(R.string.reminders),
                vm = vm
            )
        }

        item { Spacer(Modifier.height(15.dp)) }

        item {
            ValidateCancelButtons(
                onValidate = {
                    scope.launch {
                        note?.let {
                            val cleanedChecklist = (it.checklist ?: emptyList())
                                .map { ci -> ci.copy(text = ci.text.trim()) }
                                .filter { ci -> ci.text.isNotEmpty() }
                            vm.update(it.copy(
                                title = it.title.trim(),
                                checklist = cleanedChecklist,
                                lastEdit = System.currentTimeMillis()
                            ))
                        }
                        onSaved()
                    }
                },
                onCancel = {
                    scope.launch {
                        note?.let {
                            if (it.title.isBlank() && it.desc.isBlank()) vm.delete(it)
                        }
                        onCancel()
                    }
                }
            )
        }
    }
}
