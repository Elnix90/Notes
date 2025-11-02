package org.elnix.notes.ui.helpers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.notes.R
import org.elnix.notes.data.ReminderEntity
import org.elnix.notes.ui.NoteViewModel
import org.elnix.notes.utils.ReminderBubble
import org.elnix.notes.utils.ReminderPicker
import org.elnix.notes.utils.cancelReminderNotification
import org.elnix.notes.utils.scheduleReminderNotification

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RemindersSection(
    reminders: List<ReminderEntity>,
    currentId: Long?,
    title: String,
    vm: NoteViewModel
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val reminderText = stringResource(R.string.reminder)

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        itemVerticalAlignment = Alignment.CenterVertically
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

        // The "Add new reminder" picker
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
}
