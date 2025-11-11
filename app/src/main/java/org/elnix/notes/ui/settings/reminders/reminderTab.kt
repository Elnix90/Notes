package org.elnix.notes.ui.settings.reminders

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.elnix.notes.R
import org.elnix.notes.data.ReminderEntity
import org.elnix.notes.data.settings.stores.ReminderSettingsStore
import org.elnix.notes.data.settings.stores.ReminderSettingsStore.getDefaultRemindersFlow
import org.elnix.notes.data.settings.stores.ReminderSettingsStore.setDefaultReminders
import org.elnix.notes.ui.helpers.reminders.ReminderBubble
import org.elnix.notes.ui.helpers.reminders.ReminderPicker
import org.elnix.notes.ui.settings.SettingsLazyHeader

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun RemindersTab(ctx: Context, scope: CoroutineScope, onBack: (() -> Unit)) {
    val defaultReminders by getDefaultRemindersFlow(ctx)
        .collectAsState(initial = emptyList())

    var showHelpDialog by remember { mutableStateOf(false) }

    SettingsLazyHeader(
        title = stringResource(R.string.security_privacy),
        onBack = onBack,
        helpText = stringResource(R.string.reminders_help_text),
        onReset = {
            scope.launch {
                ReminderSettingsStore.resetAll(ctx)
            }
        }
    ) {

        item {
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
                                val newList =
                                    defaultReminders.toMutableList().apply { removeAt(index) }
                                scope.launch { setDefaultReminders(ctx, newList) }
                            }
                        )
                    }
            }
        }


        item {
            ReminderPicker { picked ->
                val newList = defaultReminders + picked
                scope.launch { setDefaultReminders(ctx, newList) }
            }
        }
    }

    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = {
                showHelpDialog = false
            },
            confirmButton = {
                TextButton(
                    onClick = { showHelpDialog = false}
                ) {
                    Text(
                        text = "Ok"
                    )
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Help,
                    contentDescription = "Help Icon",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = {
                Text(
                    text = stringResource(R.string.reminders_help),
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.reminders_help_text),
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

