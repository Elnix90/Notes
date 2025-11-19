package org.elnix.notes.ui.helpers.reminders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.launch
import org.elnix.notes.R
import org.elnix.notes.data.NoteEntity
import org.elnix.notes.data.ReminderEntity
import org.elnix.notes.data.settings.stores.ReminderSettingsStore
import org.elnix.notes.ui.NoteViewModel
import org.elnix.notes.ui.theme.AppObjectsColors
import org.elnix.notes.utils.ReminderOffset
import org.elnix.notes.utils.cancelReminderNotification
import org.elnix.notes.utils.scheduleReminderNotification
import java.util.Calendar

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RemindersSection(
    note: NoteEntity,
    reminders: List<ReminderEntity>,
    activity: FragmentActivity,
    currentId: Long?,
    title: String,
    vm: NoteViewModel
) {
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current
    val reminderText = stringResource(R.string.reminder)

    var showOffsetPicker by remember { mutableStateOf(false) }
    val allOffsets by ReminderSettingsStore.getReminders(ctx).collectAsState(initial = emptyList())

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        itemVerticalAlignment = Alignment.CenterVertically
    ) {
        reminders.sortedBy { it.dueDateTime }.forEach { reminder ->
            val offset = reminder.dueDateTime
            TimeBubble(
               reminderOffset = ReminderOffset(
                    year = offset.get(Calendar.YEAR),
                    month = offset.get(Calendar.MONTH),
                    dayOfMonth = offset.get(Calendar.DAY_OF_MONTH),
                    hourOfDay = offset.get(Calendar.HOUR_OF_DAY),
                    minute = offset.get(Calendar.MINUTE)
                ),
                onClick = {
                    scope.launch {
                        val updatedReminder = reminder.copy(enabled = !reminder.enabled)
                        vm.updateReminder(updatedReminder)
                        if (reminder.enabled) {
                            scheduleReminderNotification(
                                context = ctx,
                                reminder = updatedReminder,
                                note = note,
                                title = title.ifBlank { reminderText }
                            )
                        } else {
                            cancelReminderNotification(ctx, reminder.id)
                        }
                    }
                },
                onDelete = {
                    scope.launch {
                        vm.deleteReminder(reminder)
                        cancelReminderNotification(ctx, reminder.id)
                    }
                },
                enabled = reminder.enabled
            )
        }
        IconButton(
            onClick = { showOffsetPicker = true },
            colors = AppObjectsColors.iconButtonColors()
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.pick_a_offset),
            )
        }
    }

    if (showOffsetPicker) {
        OffsetPickerDialog(
            offsets = allOffsets,
            activity = activity,
            onDismiss = { showOffsetPicker = false }
        ) { picked ->
            currentId?.let { noteId ->
                val reminderEntity = ReminderEntity(
                    noteId = noteId,
                    dueDateTime = picked.toCalendar(),
                    enabled = true
                )
                scope.launch {
                    val id = vm.addReminder(reminderEntity)
                    scheduleReminderNotification(
                        context = ctx,
                        reminder = reminderEntity.copy(id = id),
                        note = note,
                        title = title.ifBlank { reminderText }
                    )
                }
            }
            showOffsetPicker = false
        }
    }
}
