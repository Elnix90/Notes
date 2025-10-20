package org.elnix.notes

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.notes.data.NoteEntity
import org.elnix.notes.ui.NoteViewModel
import org.elnix.notes.utils.scheduleNotification
import java.util.Calendar
import java.util.concurrent.TimeUnit

@Composable
fun NoteEditorScreen(
    vm: NoteViewModel,
    noteId: Long? = null,
    onSaved: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var note by remember { mutableStateOf<NoteEntity?>(null) }
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var dueDateTime by remember { mutableStateOf<Calendar?>(null) }
    var reminderEnabled by remember { mutableStateOf(false) }

    // Temp calendar for pickers
    var tempDate by remember { mutableStateOf<Calendar?>(null) }

    // Load existing note if editing
    LaunchedEffect(noteId) {
        if (noteId != null) {
            val n = vm.getById(noteId)
            note = n
            n?.let {
                title = it.title
                desc = it.desc
                dueDateTime = it.dueDateTime
                reminderEnabled = it.reminderEnabled
            }
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
            modifier = Modifier.fillMaxWidth().weight(1f, fill = false)
        )

        // Date/time picker row
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = {
                    // Step 1: pick date
                    val cal = Calendar.getInstance()
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            cal.set(Calendar.YEAR, year)
                            cal.set(Calendar.MONTH, month)
                            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                            tempDate = cal

                            // Step 2: pick time immediately
                            TimePickerDialog(
                                context,
                                { _, hourOfDay, minute ->
                                    tempDate?.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                    tempDate?.set(Calendar.MINUTE, minute)
                                    tempDate?.set(Calendar.SECOND, 0)
                                    dueDateTime = tempDate
                                },
                                cal.get(Calendar.HOUR_OF_DAY),
                                cal.get(Calendar.MINUTE),
                                true
                            ).show()

                        },
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                    ).show()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onBackground
                )
            ) {
                Text(
                    text = dueDateTime?.let { "Due: ${it.time}" } ?: "Pick Date/Time"
                )
            }

            Spacer(Modifier.width(12.dp))

            // Remaining time text
            dueDateTime?.let {
                Text(getRemainingTimeText(it))
            }
        }

        // Reminder toggle
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Enable Reminder")
            Spacer(Modifier.width(8.dp))
            Switch(
                checked = reminderEnabled,
                onCheckedChange = { reminderEnabled = it }
            )
        }

        Button(
            onClick = {
                scope.launch {
                    if (note != null) {
                        vm.update(
                            note!!.copy(
                                title = title,
                                desc = desc,
                                dueDateTime = dueDateTime,
                                reminderEnabled = reminderEnabled
                            )
                        )
                    } else {
                        vm.addNote(title, desc, dueDateTime, reminderEnabled)
                    }

                    // Schedule notification if enabled
                    if (reminderEnabled && dueDateTime != null) {
                        scheduleNotification(context, title, dueDateTime!!)
                    }

                    onSaved()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onBackground
            )
        ) {
            Text(if (noteId == null) "Save" else "Update")
        }
    }
}



// Simple remaining time calculator
private fun getRemainingTimeText(due: Calendar): String {
    val now = Calendar.getInstance()
    val diff = due.timeInMillis - now.timeInMillis
    if (diff <= 0) return "Due time passed"

    val days = TimeUnit.MILLISECONDS.toDays(diff)
    val hours = TimeUnit.MILLISECONDS.toHours(diff) % 24
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60

    return buildString {
        append("in ")
        if (days > 0) append("${days}d ")
        if (hours > 0) append("${hours}h ")
        if (minutes > 0) append("${minutes}m")
    }
}


