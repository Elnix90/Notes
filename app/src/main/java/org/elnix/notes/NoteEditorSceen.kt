package org.elnix.notes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import java.util.Calendar
import java.util.concurrent.TimeUnit

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

    // List of reminders for this note
    var reminders by remember { mutableStateOf(listOf<ReminderEntity>()) }

    // Compose dialog states
    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = rememberMaterialDialogState()
    var tempCalendar by remember { mutableStateOf(Calendar.getInstance()) }

    // Load note and reminders
    LaunchedEffect(noteId) {
        if (noteId != null) {
            val n = vm.getById(noteId)
            note = n
            n?.let {
                title = it.title
                desc = it.desc
            }
            reminders = vm.getReminders(noteId)
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
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = desc,
            onValueChange = { desc = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        // List existing reminders
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            reminders.forEach { reminder ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Reminder at: ${reminder.dueDateTime.time}")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(
                            checked = reminder.enabled,
                            onCheckedChange = { enabled ->
                                scope.launch {
                                    vm.updateReminder(reminder.copy(enabled = enabled))
                                    reminders = vm.getReminders(noteId!!)
                                }
                            }
                        )
                        IconButton(onClick = {
                            scope.launch {
                                vm.deleteReminder(reminder)
                                reminders = vm.getReminders(noteId!!)
                            }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Reminder")
                        }
                    }
                }
            }

            // Add new reminder button
            Button(onClick = { dateDialogState.show() }) {
                Text("Add Reminder")
            }
        }

        // Date picker
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

        // Time picker
        MaterialDialog(dialogState = timeDialogState, buttons = {
            positiveButton("OK") {
                val newReminder = ReminderEntity(
                    noteId = noteId!!,
                    dueDateTime = tempCalendar,
                    enabled = true
                )
                vm.addReminder(newReminder)
                // update reminders list
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

        // Save/Update button
        Button(
            onClick = {
                scope.launch {
                    if (note != null) {
                        vm.update(note!!.copy(title = title, desc = desc))
                    } else {
                        vm.addNote(title, desc) // returns Job, but we ignore it
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

//// Helper for remaining time (optional)
//private fun getRemainingTimeText(due: Calendar): String {
//    val now = Calendar.getInstance()
//    val diff = due.timeInMillis - now.timeInMillis
//    if (diff <= 0) return "Due time passed"
//
//    val days = TimeUnit.MILLISECONDS.toDays(diff)
//    val hours = TimeUnit.MILLISECONDS.toHours(diff) % 24
//    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60
//
//    return buildString {
//        append("in ")
//        if (days > 0) append("${days}d ")
//        if (hours > 0) append("${hours}h ")
//        if (minutes > 0) append("${minutes}m")
//    }
//}
