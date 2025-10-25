package org.elnix.notes.ui.settings.security

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.notes.data.LockSettings
import org.elnix.notes.data.LockSettingsStore
import org.elnix.notes.ui.helpers.SettingsTitle

@Composable
fun SecurityTab(onBack: (() -> Unit)) {
    val ctx = LocalContext.current
    val settings by LockSettingsStore.getLockSettings(ctx).collectAsState(initial = LockSettings())
    val scope = rememberCoroutineScope()

    Column(Modifier.padding(16.dp)) {
        SettingsTitle("Security", onBack)

        Spacer(Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Enable Biometrics")
            Switch(checked = settings.useBiometrics, onCheckedChange = {
                scope.launch { LockSettingsStore.updateLockSettings(ctx, settings.copy(useBiometrics = it)) }
            })
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Enable Device Credential")
            Switch(checked = settings.useDeviceCredential, onCheckedChange = {
                scope.launch { LockSettingsStore.updateLockSettings(ctx, settings.copy(useDeviceCredential = it)) }
            })
        }

        Spacer(Modifier.height(12.dp))
        Text("Lock Timeout (minutes)")
        Slider(
            value = settings.lockTimeoutMinutes.toFloat(),
            onValueChange = {
                scope.launch {
                    LockSettingsStore.updateLockSettings(ctx, settings.copy(lockTimeoutMinutes = it.toInt()))
                }
            },
            valueRange = 1f..30f,
            steps = 29
        )
        Text("${settings.lockTimeoutMinutes} min")
    }
}
