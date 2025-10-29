package org.elnix.notes.ui.settings

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.elnix.notes.data.ReminderEntity
import org.elnix.notes.data.settings.stores.ReminderSettingsStore.getDefaultRemindersFlow
import org.elnix.notes.data.settings.stores.ReminderSettingsStore.setDefaultReminders
import org.elnix.notes.ui.helpers.SettingsTitle
import org.elnix.notes.utils.ReminderBubble
import org.elnix.notes.utils.ReminderPicker

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun RemindersTab(ctx: Context, scope: CoroutineScope, onBack: (() -> Unit)) {
    val defaultReminders by getDefaultRemindersFlow(ctx)
        .collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SettingsTitle("Default Reminders", onBack)

        // Reminders Row
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
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
                            scope.launch { setDefaultReminders(ctx, newList) }
                        }
                    )
                }
        }


        ReminderPicker { picked ->
            val newList = defaultReminders + picked
            scope.launch { setDefaultReminders(ctx, newList) }
        }
    }
}

