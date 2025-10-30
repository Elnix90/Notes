package org.elnix.notes.ui.helpers

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import org.elnix.notes.R
import org.elnix.notes.ui.theme.AppObjectsColors
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
        Text(stringResource(R.string.add_reminder))
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
                ) { Text(stringResource(R.string.next)) }
            },
            dismissButton = {
                TextButton(onClick = { showDate = false }) { Text(stringResource(R.string.cancel)) }
            },
            colors = AppObjectsColors.datePickerColors()
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
                ) { Text(stringResource(R.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showTime = false
                    showDate = true
                }) { Text(stringResource(R.string.previous)) }
            },
            title = { Text(stringResource(R.string.select_time)) },
            text = { TimePicker(state = timePickerState) },
            containerColor = MaterialTheme.colorScheme.surface,
            iconContentColor = MaterialTheme.colorScheme.onSurface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurface
        )
    }
}

