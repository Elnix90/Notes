package org.elnix.notes.ui.settings.debug

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.elnix.notes.ui.NoteViewModel
import org.elnix.notes.ui.helpers.settings.SettingsTitle
import org.elnix.notes.ui.theme.AppObjectsColors

@Composable
fun RemindersDebugTab(ctx: Context, scope: CoroutineScope, vm: NoteViewModel, onBack: (() -> Unit)) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                WindowInsets.systemBars
                    .asPaddingValues()
            )
            .padding(horizontal = 16.dp, vertical = 5.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SettingsTitle(title = "Debug -> Reminders", onBack = onBack)

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

            Button(
                onClick = { scope.launch { vm.disableAllReminders() } },
                modifier = Modifier.fillMaxWidth(),
                colors = AppObjectsColors.buttonColors()
            ) {
                Text(
                    text ="Disable All Reminders",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

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

            Button(
                onClick = { vm.cancelAllPendingNotifications(ctx) },
                modifier = Modifier.fillMaxWidth(),
                colors = AppObjectsColors.buttonColors()
            ) {
                Text(text = "Cancel All Pending Notifications",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

