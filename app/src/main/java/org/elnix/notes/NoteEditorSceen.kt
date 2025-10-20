package org.elnix.notes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.launch
import org.elnix.notes.data.NoteEntity
import org.elnix.notes.data.ReminderEntity
import org.elnix.notes.ui.NoteViewModel
import org.elnix.notes.utils.ReminderBubble
import java.util.Calendar

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NoteEditorScreen(
    vm: NoteViewModel,
    noteId: Long? = null,
    onSaved: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var note by remember { mutableStateOf<NoteEntity?>(null) }
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    // Dialog states for date & time pickers
    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = rememberMaterialDialogState()
    var tempCalendar by remember { mutableStateOf(Calendar.getInstance()) }

    // Load note if editing existing one
    LaunchedEffect(noteId) {
        if (noteId != null) {
            val loaded = vm.getById(noteId)
            note = loaded
            title = loaded?.title ?: ""
            desc = loaded?.desc ?: ""
        }
    }

    // Observe reminders — only if we already have a noteId
    val reminders by remember(noteId) {
        if (noteId != null) vm.remindersFor(noteId)
        else null
    }?.collectAsState(initial = emptyList()) ?: remember { mutableStateOf(emptyList()) }

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
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = desc,
            onValueChange = { desc = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        // === Reminder section ===
        // List existing reminders as interactive bubbles
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            reminders.forEach { reminder ->
                ReminderBubble(
                    reminder = reminder,
                    onToggle = { enabled ->
                        scope.launch { vm.updateReminder(reminder.copy(enabled = enabled)) }
                    },
                    onDelete = {
                        scope.launch { vm.deleteReminder(reminder) }
                    }
                )
            }

            Button(onClick = { dateDialogState.show() }) {
                Text("Add Reminder")
            }
        }

        // === Date Picker ===
        MaterialDialog(dialogState = dateDialogState, buttons = {
            positiveButton("OK") { timeDialogState.show() }
            negativeButton("Cancel")
        }) {
            datepicker { date ->
                tempCalendar = Calendar.getInstance().apply {
                    set(date.year, date.monthValue - 1, date.dayOfMonth)
                }
            }
        }

        // === Time Picker ===
        MaterialDialog(dialogState = timeDialogState, buttons = {
            positiveButton("OK") {
                scope.launch {
                    // Create note if it doesn't exist yet
                    val id = noteId ?: vm.addNoteAndReturnId(title, desc)

                    val newReminder = ReminderEntity(
                        noteId = id,
                        dueDateTime = tempCalendar,
                        enabled = true
                    )
                    vm.addReminder(newReminder)
                }
            }
            negativeButton("Cancel")
        }) {
            timepicker { time ->
                tempCalendar.set(Calendar.HOUR_OF_DAY, time.hour)
                tempCalendar.set(Calendar.MINUTE, time.minute)
                tempCalendar.set(Calendar.SECOND, 0)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // === Save/Update Button ===
        Button(
            onClick = {
                scope.launch {
                    if (note != null) {
                        vm.update(note!!.copy(title = title, desc = desc))
                    } else {
                        vm.addNote(title, desc)
                    }
                    onSaved()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (noteId == null) "Save" else "Update")
        }
    }
}


