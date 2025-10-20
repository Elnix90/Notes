package org.elnix.notes.utils

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.util.Calendar

@Composable
fun ReminderPicker(
    onPicked: (ReminderOffset) -> Unit
) {
    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = rememberMaterialDialogState()
    var tempCalendar by remember { mutableStateOf(Calendar.getInstance()) }

    Column {
        Button(onClick = { dateDialogState.show() }) {
            Text("Pick Reminder")
        }

        MaterialDialog(dialogState = dateDialogState, buttons = {
            positiveButton("Next") { timeDialogState.show() }
            negativeButton("Cancel")
        }) {
            datepicker { date ->
                tempCalendar = Calendar.getInstance().apply {
                    set(date.year, date.monthValue - 1, date.dayOfMonth)
                }
            }
        }

        MaterialDialog(dialogState = timeDialogState, buttons = {
            positiveButton("OK") {
                val offset = ReminderOffset(
                    hourOfDay = tempCalendar.get(Calendar.HOUR_OF_DAY),
                    minute = tempCalendar.get(Calendar.MINUTE)
                )
                onPicked(offset)
            }
            negativeButton("Cancel")
        }) {
            timepicker { time ->
                tempCalendar.set(Calendar.HOUR_OF_DAY, time.hour)
                tempCalendar.set(Calendar.MINUTE, time.minute)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
