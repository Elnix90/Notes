package org.elnix.notes.ui.editors

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.notes.R
import org.elnix.notes.data.NoteEntity
import org.elnix.notes.data.helpers.NoteType
import org.elnix.notes.ui.NoteViewModel
import org.elnix.notes.ui.helpers.CompletionToggle
import org.elnix.notes.ui.helpers.RemindersSection
import org.elnix.notes.ui.helpers.TextDivider
import org.elnix.notes.ui.helpers.ValidateCancelButtons
import org.elnix.notes.ui.helpers.colors.NotesColorPickerSection
import org.elnix.notes.ui.helpers.colors.setRandomColor
import org.elnix.notes.ui.helpers.colors.toggleAutoColor
import org.elnix.notes.ui.helpers.colors.updateNoteBgColor
import org.elnix.notes.ui.helpers.colors.updateNoteTextColor
import org.elnix.notes.ui.theme.AppObjectsColors

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NoteEditorScreen(
    vm: NoteViewModel,
    noteId: Long?,
    onSaved: () -> Unit,
    onCancel: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()


    var note by remember { mutableStateOf<NoteEntity?>(null) }
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var createdNoteId by remember { mutableStateOf<Long?>(null) }

    // Load or create note
    LaunchedEffect(noteId) {
        if (noteId != null) {
            val loaded = vm.getById(noteId)
            note = loaded
            title = loaded?.title ?: ""
            desc = loaded?.desc ?: ""
        } else if (createdNoteId == null) {
            val id = vm.addNoteAndReturnId(type = NoteType.TEXT)
            createdNoteId = id
            note = vm.getById(id)
        }
    }

    val currentId = note?.id ?: createdNoteId



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

        val reminderText = stringResource(R.string.reminders)

        val reminders by remember(currentId) {
            if (currentId != null) vm.remindersFor(currentId) else null
        }?.collectAsState(initial = emptyList()) ?: remember { mutableStateOf(emptyList()) }


        TextDivider(reminderText)

        RemindersSection(
            reminders = reminders,
            currentId = currentId,
            title = title,
            vm = vm
        )

        TextDivider(stringResource(R.string.color_text_literal))


        NotesColorPickerSection(
            note,
            scope,
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


        TextDivider(stringResource(R.string.quick_actions))

        CompletionToggle(
            note = note,
            currentId = currentId,
            vm = vm,
            onUpdated = { note = it }
        )

        Spacer(Modifier.height(15.dp))

        ValidateCancelButtons(
            onValidate = {
                scope.launch {
                    note?.let { vm.update(it.copy(title = title.trim(), desc = desc.trim(), lastEdit = System.currentTimeMillis())) }
                    onSaved()
                }
            },
            onCancel = {
                scope.launch {
                    note?.let {
                        if (it.title.isBlank() && it.desc.isBlank()) {
                            vm.delete(it)
                        }
                    }
                    onCancel()
                }
            }
        )

    }
}
