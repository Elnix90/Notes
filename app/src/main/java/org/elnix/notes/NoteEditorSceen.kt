package org.elnix.notes

import android.app.TimePickerDialog
import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.util.*

@Composable
fun NoteEditorScreen(onSave: (String, String, Calendar?, Boolean) -> Unit) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // Due date/time
    var dueDateTime by remember { mutableStateOf<Calendar?>(null) }
    var reminderEnabled by remember { mutableStateOf(false) }

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
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
        )

        // Due date/time picker
        Button(onClick = {
            val cal = Calendar.getInstance()
            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    TimePickerDialog(
                        context,
                        { _, hour, minute ->
                            cal.set(year, month, dayOfMonth, hour, minute)
                            dueDateTime = cal
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
        }) {
            Text(
                text = dueDateTime?.let { "Due: ${it.time}" } ?: "Select Due Date/Time"
            )
        }

        // Reminder toggle
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Enable Reminder")
            Spacer(Modifier.width(8.dp))
            Switch(
                checked = reminderEnabled,
                onCheckedChange = { reminderEnabled = it }
            )
        }

        Button(
            onClick = { onSave(title, desc, dueDateTime, reminderEnabled) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }
    }
}
