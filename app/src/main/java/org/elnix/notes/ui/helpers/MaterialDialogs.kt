package org.elnix.notes.ui.helpers

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.elnix.notes.utils.ReminderOffset
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StyledReminderDialogs(
    tempCalendar: Calendar,
    onPicked: (ReminderOffset) -> Unit
) {
    var showDate by remember { mutableStateOf(false) }
    var showTime by remember { mutableStateOf(false) }

    Button(onClick = { showDate = true }) {
        Text("Add Reminder")
    }

    if (showDate) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = tempCalendar.timeInMillis
        )

        DatePickerDialog(
            onDismissRequest = { showDate = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis
                        if (millis != null) tempCalendar.timeInMillis = millis
                        showDate = false
                        showTime = true
                    }
                ) { Text("Next") }
            },
            dismissButton = {
                TextButton(onClick = { showDate = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTime) {
        val timePickerState = rememberTimePickerState(
            initialHour = tempCalendar.get(Calendar.HOUR_OF_DAY),
            initialMinute = tempCalendar.get(Calendar.MINUTE),
            is24Hour = true
        )

        AlertDialog(
            onDismissRequest = { showTime = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        tempCalendar.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                        tempCalendar.set(Calendar.MINUTE, timePickerState.minute)
                        showTime = false
                        onPicked(
                            ReminderOffset(
                                hourOfDay = timePickerState.hour,
                                minute = timePickerState.minute
                            )
                        )
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showTime = false
                    showDate = true
                }) { Text("Previous") }
            },
            title = { Text("Select Time") },
            text = { TimePicker(state = timePickerState) }
        )
    }
}

