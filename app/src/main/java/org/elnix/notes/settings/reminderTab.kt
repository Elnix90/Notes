package org.elnix.notes.settings

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.elnix.notes.data.ReminderEntity
import org.elnix.notes.data.SettingsStore
import org.elnix.notes.utils.ReminderBubble
import org.elnix.notes.utils.ReminderPicker


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RemindersTab(ctx: Context, scope: CoroutineScope) {
    val defaultReminders by SettingsStore.getDefaultRemindersFlow(ctx)
        .collectAsState(initial = emptyList())

    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            defaultReminders.sortedBy { it.toCalendar().timeInMillis }
                .forEachIndexed { index, reminder ->
                    ReminderBubble(
                        reminder = ReminderEntity(
                            noteId = -1,
                            dueDateTime = reminder.toCalendar(),
                            enabled = true
                        ),
                        onToggle = {},
                        onDelete = {
                            val newList = defaultReminders.toMutableList().apply { removeAt(index) }
                            scope.launch { SettingsStore.setDefaultReminders(ctx, newList) }
                        }
                    )
                }
        }

        ReminderPicker { picked ->
            val newList = defaultReminders + picked
            scope.launch { SettingsStore.setDefaultReminders(ctx, newList) }
        }
    }
}