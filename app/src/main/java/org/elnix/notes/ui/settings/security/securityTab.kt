package org.elnix.notes.ui.settings.security

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.notes.data.LockSettings
import org.elnix.notes.data.LockSettingsStore
import org.elnix.notes.ui.helpers.SettingsOutlinedField
import org.elnix.notes.ui.helpers.SettingsTitle
import org.elnix.notes.ui.helpers.SwitchRow

@Composable
fun SecurityTab(onBack: (() -> Unit)) {
    val ctx = LocalContext.current
    val settings by LockSettingsStore.getLockSettings(ctx).collectAsState(initial = LockSettings())
    val scope = rememberCoroutineScope()

    Column(Modifier.padding(16.dp)) {
        SettingsTitle("Security", onBack)

        Spacer(Modifier.height(8.dp))

        SwitchRow(
            settings.useBiometrics,
            "Enable Biometrics",
        ) {
            scope.launch {
                LockSettingsStore.updateLockSettings(
                    ctx,
                    settings.copy(useBiometrics = it)
                )
            }
        }

        SwitchRow(
            settings.useDeviceCredential,
            "Enable Device Credential",
        ) {
            scope.launch {
                LockSettingsStore.updateLockSettings(
                    ctx,
                    settings.copy(useDeviceCredential = it)
                )
            }
        }

        Spacer(Modifier.height(12.dp))


        SettingsOutlinedField(
            value = settings.lockTimeoutMinutes.toString(),
            label = "Timeout in minutes",
            minValue = 0,
            maxValue = 60,
            keyboardType = KeyboardType.Number,
            scope = scope
        ) { newValue ->
            val parsed = newValue.toIntOrNull() ?: return@SettingsOutlinedField
            LockSettingsStore.updateLockSettings(ctx, settings.copy(lockTimeoutMinutes = parsed))
        }
    }
}
