package org.elnix.notes.ui.settings.reminders


import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.elnix.notes.R
import org.elnix.notes.data.settings.stores.NotificationActionType
import org.elnix.notes.data.settings.stores.NotificationsSettingsStore
import org.elnix.notes.data.settings.stores.ReminderSettingsStore
import org.elnix.notes.ui.helpers.settings.SettingsLazyHeader

@Composable
fun NotificationsCustomisationTab(
    ctx: Context,
    scope: CoroutineScope,
    onBack: (() -> Unit)
) {
    val settings by NotificationsSettingsStore.getSettingsFlow(ctx).collectAsState(initial = emptyList())

    SettingsLazyHeader(
        title = stringResource(R.string.notification_reminders),
        onBack = onBack,
        helpText = stringResource(R.string.reminders_help_text),
        onReset = {
            scope.launch {
                ReminderSettingsStore.resetAll(ctx)
            }
        }
    ) {

        items(settings) { action ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(action.actionType.name.replace("_", " "))
                when(action.actionType) {
                    NotificationActionType.SNOOZE -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            var snoozeText by remember { mutableStateOf(action.snoozeMinutes.toString()) }
                            TextField(
                                value = snoozeText,
                                onValueChange = { snoozeText = it },
                                label = { Text("Snooze (min)") },
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.width(100.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Switch(
                                checked = action.enabled,
                                onCheckedChange = { enabled ->
                                    scope.launch { NotificationsSettingsStore.setEnabled(ctx, action.actionType, enabled) }
                                }
                            )
                            Button(onClick = {
                                snoozeText.toIntOrNull()?.let { minutes ->
                                    scope.launch { NotificationsSettingsStore.setSnoozeDuration(ctx, minutes) }
                                }
                            }) {
                                Text("Save")
                            }
                        }
                    }
                    else -> {
                        Switch(
                            checked = action.enabled,
                            onCheckedChange = { enabled ->
                                scope.launch { NotificationsSettingsStore.setEnabled(ctx, action.actionType, enabled) }
                            }
                        )
                    }
                }
            }
        }
    }
}
