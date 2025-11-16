package org.elnix.notes.ui.helpers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.elnix.notes.R
import org.elnix.notes.ui.theme.AppObjectsColors
import org.elnix.notes.utils.ReminderOffset
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StyledReminderDialogs(
    initialMillis: Long,
    showText: Boolean,
    onPicked: (ReminderOffset) -> Unit
) {
    var showDate by remember { mutableStateOf(false) }
    var showTime by remember { mutableStateOf(false) }

    // Store combined Calendar time as mutable state
    var pickedCalendar by remember {
        mutableStateOf(Calendar.getInstance().apply { timeInMillis = initialMillis })
    }

    Button(
        onClick = { showDate = true },
        colors = AppObjectsColors.buttonColors()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = stringResource(R.string.custom_date)
            )
            if (showText) {
                Text(stringResource(R.string.pick_a_reminder_date),)
            }
        }
    }

    if (showDate) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = pickedCalendar.timeInMillis)
        DatePickerDialog(
            onDismissRequest = { showDate = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { selectedMillis ->
                        pickedCalendar.timeInMillis = selectedMillis
                        showDate = false
                        showTime = true
                    }
                }) { Text(stringResource(R.string.next)) }
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
            initialHour = pickedCalendar.get(Calendar.HOUR_OF_DAY),
            initialMinute = pickedCalendar.get(Calendar.MINUTE),
            is24Hour = true
        )

        AlertDialog(
            onDismissRequest = { showTime = false },
            confirmButton = {
                TextButton(onClick = {
                    pickedCalendar.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                    pickedCalendar.set(Calendar.MINUTE, timePickerState.minute)
                    pickedCalendar.set(Calendar.SECOND, 0)
                    pickedCalendar.set(Calendar.MILLISECOND, 0)
                    showTime = false

                    // Calculate seconds from now to picked time
                    val now = Calendar.getInstance()
                    val diffSeconds = ((pickedCalendar.timeInMillis - now.timeInMillis) / 1000).coerceAtLeast(0)

                    onPicked(
                        ReminderOffset(
                            secondsFromNow = diffSeconds
                        )
                    )
                }) { Text(stringResource(R.string.ok)) }
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

