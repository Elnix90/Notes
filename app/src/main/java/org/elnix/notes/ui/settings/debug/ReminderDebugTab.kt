package org.elnix.notes.ui.settings.debug

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.elnix.notes.ui.NoteViewModel
import org.elnix.notes.ui.helpers.SettingsTitle

@Composable
fun RemindersDebugTab(ctx: Context, scope: CoroutineScope, vm: NoteViewModel, onBack: (() -> Unit)) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SettingsTitle("Debug -> Reminders", onBack)

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

            Button(
                onClick = { scope.launch { vm.disableAllReminders() } },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text ="Disable All Reminders",
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Button(
                onClick = { scope.launch { vm.enableAllReminders() } },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Enable All Reminders",
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Button(
                onClick = { scope.launch { vm.deleteAllReminders() } },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Delete All Reminders",
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Button(
                onClick = { vm.cancelAllPendingNotifications(ctx) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Cancel All Pending Notifications",
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

