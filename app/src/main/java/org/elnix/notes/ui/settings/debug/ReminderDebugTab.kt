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
import org.elnix.notes.ui.NoteViewModel
import org.elnix.notes.ui.settings.SettingsLazyHeader
import org.elnix.notes.ui.theme.AppObjectsColors

@Composable
fun RemindersDebugTab(ctx: Context, scope: CoroutineScope, vm: NoteViewModel, onBack: (() -> Unit)) {
    SettingsLazyHeader(
        title = "Debug -> Reminders",
        onBack = onBack
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
    }
}

