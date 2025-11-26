package org.elnix.notes.ui.helpers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SelectableDates
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.elnix.notes.R
import org.elnix.notes.ui.helpers.reminders.TimeBubble
import org.elnix.notes.ui.theme.AppObjectsColors
import org.elnix.notes.utils.ReminderOffset
import org.elnix.notes.utils.toReminderOffset
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StyledReminderDialogs(
    initialOffset: ReminderOffset? = null,
    onDismiss: () -> Unit,
    onPicked: (ReminderOffset) -> Unit
) {
    var showTime by remember { mutableStateOf(false) }
    var showAtIn by remember { mutableStateOf(false) }

    var atSelected by remember { mutableStateOf(true) }

    val pickedCal = remember {
        initialOffset?.toCalendar()
            ?: Calendar.getInstance().apply {
                val currentMinute = get(Calendar.MINUTE)
                val nextHour = if (currentMinute == 0) get(Calendar.HOUR_OF_DAY) else get(Calendar.HOUR_OF_DAY) + 1
                set(Calendar.HOUR_OF_DAY, nextHour)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
    }


    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis


    /* DATE PICKER */
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = pickedCal.timeInMillis,
                selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= today
            }
        }
    )

    DatePickerDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(
                onClick = {
                    datePickerState.selectedDateMillis?.let { date ->
                        val calDate = Calendar.getInstance().apply {
                            timeInMillis = date
                        }
                        pickedCal.set(Calendar.YEAR, calDate.get(Calendar.YEAR))
                        pickedCal.set(Calendar.MONTH, calDate.get(Calendar.MONTH))
                        pickedCal.set(Calendar.DAY_OF_MONTH, calDate.get(Calendar.DAY_OF_MONTH))

                        showTime = true
                    }
                },
                enabled = (datePickerState.selectedDateMillis ?: -1L) > today,
                colors = AppObjectsColors.buttonColors(),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(end = 15.dp, bottom = 15.dp)
            ) {
                Text(stringResource(R.string.next))
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismiss() },
                colors = AppObjectsColors.cancelButtonColors(),
                modifier = Modifier.padding(end = 5.dp, bottom = 15.dp)
            ) {
                Text(stringResource(R.string.cancel))
            }
        },
        colors = AppObjectsColors.datePickerColors()
    ) {
        DatePicker(
            colors = AppObjectsColors.datePickerColors(),
            state = datePickerState
        )
    }


    /* TIME PICKER */
    if (showTime) {
        val timePickerState = rememberTimePickerState(
            initialHour = pickedCal.get(Calendar.HOUR_OF_DAY),
            initialMinute = pickedCal.get(Calendar.MINUTE),
            is24Hour = true
        )

        val now = Calendar.getInstance()
        val isToday =
            pickedCal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                    pickedCal.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)

        val minHour = now.get(Calendar.HOUR_OF_DAY)
        val minMinute = now.get(Calendar.MINUTE)

        val timeValid = !isToday ||
                (timePickerState.hour > minHour ||
                        (timePickerState.hour == minHour && timePickerState.minute > minMinute))

        AlertDialog(
            containerColor = MaterialTheme.colorScheme.surface,
            onDismissRequest = { showTime = false },
            confirmButton = {
                Button(
                    onClick = {
                        pickedCal.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                        pickedCal.set(Calendar.MINUTE, timePickerState.minute)
                        pickedCal.set(Calendar.SECOND, 0)
                        pickedCal.set(Calendar.MILLISECOND, 0)

                        showTime = false
                        showAtIn = true
                    },
                    enabled = timeValid,
                    colors = AppObjectsColors.buttonColors(),
                    shape = RoundedCornerShape(12.dp)
                ) { Text(stringResource(R.string.next)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showTime = false
                    }
                ) { Text(stringResource(R.string.previous)) }
            },
            title = { Text(stringResource(R.string.select_time)) },
            text = {
                TimePicker(
                    state = timePickerState,
                    colors = AppObjectsColors.timePickerColors()
                )
            }
        )
    }

    /* AT / IN DIALOG */
    if (showAtIn) {

        val now = System.currentTimeMillis()
        val diffSec = ((pickedCal.timeInMillis - now) / 1000).coerceAtLeast(0)

        AlertDialog(
            containerColor = MaterialTheme.colorScheme.surface,
            onDismissRequest = { showAtIn = false },
            confirmButton = {
                Button(
                    onClick = {
                        val result =
                            if (atSelected) {
                                pickedCal.toReminderOffset()
                            } else {
                                ReminderOffset(secondsFromNow = diffSec)
                            }

                        onPicked(result)
                        showAtIn = false
                    },
                    enabled = pickedCal.timeInMillis > System.currentTimeMillis(),
                    colors = AppObjectsColors.buttonColors(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.next))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAtIn = false
                    showTime = true
                }) { Text(stringResource(R.string.previous)) }
            },
            title = { Text("At or In?") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                    /* --- AT OPTION --- */
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        RadioButton(
                            selected = atSelected,
                            onClick = { atSelected = true },
                            colors = AppObjectsColors.radioButtonColors()
                        )
                        Text("At")

                        TimeBubble(
                            reminderOffset = pickedCal.toReminderOffset(),
                            enabled = true,
                            expandToLargerUnits = true
                        )
                    }

                    /* --- IN OPTION --- */
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        RadioButton(
                            selected = !atSelected,
                            onClick = { atSelected = false },
                            colors = AppObjectsColors.radioButtonColors()
                        )
                        Text("In")

                        TimeBubble(
                            reminderOffset = ReminderOffset(secondsFromNow = diffSec),
                            enabled = true,
                            showAbsoluteDate = false,
                            expandToLargerUnits = true
                        )
                    }
                }
            }
        )
    }
}
