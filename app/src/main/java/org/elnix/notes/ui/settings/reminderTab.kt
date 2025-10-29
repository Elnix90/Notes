package org.elnix.notes.ui.settings

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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

    var showHelpDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Row(
            Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            SettingsTitle(
                title ="Notifications / Reminders",
                helpIcon = { showHelpDialog = true },
                onBack = onBack
            )

        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

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
                    text = "Reminders help",
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    text = "Those are the default reminders that automatically add when creating a new note, to avoid creating them again and again. You can for example set a reminder to 19h, to rapidly create a note and add a reminder when you return home",
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

