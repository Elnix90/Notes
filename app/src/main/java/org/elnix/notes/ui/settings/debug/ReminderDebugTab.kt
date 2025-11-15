package org.elnix.notes.ui.settings.debug

import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.elnix.notes.data.NoteEntity
import org.elnix.notes.data.ReminderEntity
import org.elnix.notes.ui.NoteViewModel
import org.elnix.notes.ui.helpers.settings.SettingsLazyHeader
import org.elnix.notes.ui.theme.AppObjectsColors
import org.elnix.notes.utils.scheduleReminderNotification
import java.util.Calendar

@Composable
fun RemindersDebugTab(ctx: Context, scope: CoroutineScope, vm: NoteViewModel, onBack: (() -> Unit)) {
    SettingsLazyHeader(
        title = "Debug -> Reminders",
        onBack = onBack,
        helpText = "Debug, too busy to make a translated explanation",
        onReset = null,
        resetText = null
    ) {

        item {
            Button(
                onClick = { scope.launch { vm.disableAllReminders() } },
                modifier = Modifier.fillMaxWidth(),
                colors = AppObjectsColors.buttonColors()
            ) {
                Text(
                    text = "Disable All Reminders",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        item {
            Button(
                onClick = { scope.launch { vm.enableAllReminders() } },
                modifier = Modifier.fillMaxWidth(),
                colors = AppObjectsColors.buttonColors()
            ) {
                Text(
                    text = "Enable All Reminders",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        item {
            Button(
                onClick = { scope.launch { vm.deleteAllReminders() } },
                modifier = Modifier.fillMaxWidth(),
                colors = AppObjectsColors.buttonColors()
            ) {
                Text(
                    text = "Delete All Reminders",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        item {
            Button(
                onClick = { vm.cancelAllPendingNotifications(ctx) },
                modifier = Modifier.fillMaxWidth(),
                colors = AppObjectsColors.buttonColors()
            ) {
                Text(
                    text = "Cancel All Pending Notifications",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        item {

            Button(
                onClick = {
                    scope.launch{
                        val noteId = vm.createFakeNotes(1).first()

                        scheduleReminderNotification(
                            context = ctx,
                            reminder = ReminderEntity(
                                noteId = noteId,
                                dueDateTime = Calendar.getInstance().apply {
                                    add(Calendar.SECOND, 1)
                                }
                            ),
                            note = NoteEntity(),
                            title = "Test"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = AppObjectsColors.buttonColors()
            ) {
                Text("Create fake Note and trigger notification now")
            }
        }
    }
}

