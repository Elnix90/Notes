package org.elnix.notes

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.notes.data.NoteEntity
import org.elnix.notes.data.ReminderEntity
import org.elnix.notes.ui.NoteViewModel
import org.elnix.notes.ui.theme.AppObjectsColors
import org.elnix.notes.utils.ReminderBubble
import org.elnix.notes.utils.ReminderPicker
import org.elnix.notes.utils.cancelReminderNotification
import org.elnix.notes.utils.scheduleReminderNotification

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NoteEditorScreen(
    vm: NoteViewModel,
    noteId: Long?,
    onSaved: () -> Unit,
    onCancel: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
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
            val id = vm.addNoteAndReturnId()
            createdNoteId = id
            note = vm.getById(id)
        }
    }

    val currentId = note?.id ?: createdNoteId

    val reminders by remember(currentId) {
        if (currentId != null) vm.remindersFor(currentId) else null
    }?.collectAsState(initial = emptyList()) ?: remember { mutableStateOf(emptyList()) }

    // Auto-delete empty new note when leaving the screen
    DisposableEffect(Unit) {
        onDispose {
            if (createdNoteId != null) {
                scope.launch {
                    val n = vm.getById(createdNoteId!!)
                    if (n != null && n.title.isBlank() && n.desc.isBlank()) {
                        vm.delete(n)
                    }
                }
            }
        }
    }

    // Handle system back press just like cancel
    BackHandler {
        scope.launch {
            val id = note?.id ?: createdNoteId
            if (id != null) {
                val n = vm.getById(id)
                if (n != null && n.title.isBlank() && n.desc.isBlank()) {
                    vm.delete(n)
                }
            }
            onCancel()
        }
    }


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

        Spacer(modifier = Modifier.height(8.dp))


        val reminderText = stringResource(R.string.reminder)

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            reminders.forEach { reminder ->
                ReminderBubble(
                    reminder = reminder,
                    onToggle = { enabled ->
                        scope.launch {
                            val updatedReminder = reminder.copy(enabled = enabled)
                            vm.updateReminder(updatedReminder)
                            if (enabled) {
                                scheduleReminderNotification(
                                    context,
                                    updatedReminder,
                                    title = title.ifBlank { reminderText }
                                )
                            } else {
                                cancelReminderNotification(context, reminder.id)
                            }
                        }
                    },
                    onDelete = {
                        scope.launch {
                             vm.deleteReminder(reminder)
                            cancelReminderNotification(context, reminder.id)
                        }
                    }
                )
            }

            ReminderPicker { picked ->
                currentId?.let { noteId ->
                    val reminderEntity = ReminderEntity(
                        noteId = noteId,
                        dueDateTime = picked.toCalendar(),
                        enabled = true
                    )
                    scope.launch {
                        val id = vm.addReminder(reminderEntity)

                        scheduleReminderNotification(
                            context,
                            reminderEntity.copy(id = id),
                            title = title.ifBlank { reminderText }
                        )
                    }
                }
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline)

        var isCompleted by remember { mutableStateOf(note?.isCompleted ?: false) }

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable {
                        isCompleted = !isCompleted
                        scope.launch {
                            currentId?.let { id ->
                                val n = vm.getById(id)
                                if (n != null) {
                                    val updated = n.copy(isCompleted = isCompleted)
                                    vm.update(updated)
                                    note = updated
                                }
                            }
                        }
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.completed),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.width(12.dp))

                Checkbox(
                    checked = isCompleted,
                    onCheckedChange = null,
                    colors = AppObjectsColors.checkboxColors()
                )

            }
        }


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    scope.launch {
                        currentId?.let { id ->
                            val n = vm.getById(id)
                            if (n != null) {
                                val updated = n.copy(
                                    title = title.trim(),
                                    desc = desc.trim()
                                )
                                if (updated.title.isBlank() && updated.desc.isBlank()) {
                                    vm.delete(updated)
                                    onCancel()
                                } else {
                                    vm.update(updated)
                                    onSaved()
                                }
                            }
                        }
                    }
                },
                colors = AppObjectsColors.buttonColors(),
                modifier = Modifier.weight(1.5f)
            ) {
                Text(
                    text = stringResource(R.string.save),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            OutlinedButton(
                onClick = {
                    scope.launch {
                        currentId?.let {
                            val n = vm.getById(it)
                            if (n != null && n.title.isBlank() && n.desc.isBlank()) {
                                vm.delete(n)
                            }
                        }
                        onCancel()
                    }
                },
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                colors = AppObjectsColors.cancelButtonColors(),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.cancel),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}




