package org.elnix.notes.ui.settings.security

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.notes.R
import org.elnix.notes.data.LockSettings
import org.elnix.notes.data.settings.TimeoutOptions
import org.elnix.notes.data.settings.stores.LockSettingsStore
import org.elnix.notes.security.BiometricManagerHelper
import org.elnix.notes.ui.helpers.ActionSelectorRow
import org.elnix.notes.ui.helpers.SettingsOutlinedField
import org.elnix.notes.ui.helpers.SettingsTitle
import org.elnix.notes.ui.helpers.SwitchRow
import org.elnix.notes.ui.theme.adjustBrightness
import java.time.Instant

@Composable
fun SecurityTab(onBack: (() -> Unit)) {
    val ctx = LocalContext.current
    val activity = ctx as androidx.fragment.app.FragmentActivity
    val settings by LockSettingsStore.getLockSettings(ctx).collectAsState(initial = LockSettings())
    val scope = rememberCoroutineScope()
    val enabled = settings.useBiometrics || settings.useDeviceCredential
    val canBiometrics = BiometricManagerHelper.canBiometrics(activity)
    val canDeviceLock = BiometricManagerHelper.canDeviceLock(activity)

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SettingsTitle(title = "Security", onBack = onBack)


        SwitchRow(
            settings.useBiometrics,
            "Enable Biometric Lock",
            canBiometrics
        ) {
            scope.launch {
                BiometricManagerHelper.authenticateUser(
                    activity = activity,
                    useBiometrics = true,
                    useDeviceCredential = false,
                    title = "Verification",
                    onSuccess = {
                        scope.launch {
                            LockSettingsStore.updateLockSettings(
                                ctx,
                                settings.copy(lastUnlockTimestamp = Instant.now().toEpochMilli(), useBiometrics = !settings.useBiometrics)
                            )
                        }
                    },
                    onFailure = {}
                )

            }
        }

        SwitchRow(
            settings.useDeviceCredential,
            "Enable Device Credential Lock",
            canDeviceLock
        ) {
            scope.launch {
                LockSettingsStore.updateLockSettings(
                    ctx,
                    settings.copy(useDeviceCredential = it)
                )
            }
        }


        val selectedUnit by LockSettingsStore.getUnitSelected(ctx).collectAsState(initial = TimeoutOptions.MINUTES)


        // Conversion multiplier to seconds
        val unitMultiplier = when (selectedUnit) {
            TimeoutOptions.SECONDS -> 1
            TimeoutOptions.MINUTES ->60
            TimeoutOptions.HOURS -> 3600
            TimeoutOptions.DAYS ->  86400
        }

        // Convert current seconds back to the selected display unit
        val displayedValue = remember(settings.lockTimeoutSeconds, selectedUnit) {
            (settings.lockTimeoutSeconds / unitMultiplier).toString()
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface.adjustBrightness(if (enabled) 1f else 0.5f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SettingsOutlinedField(
                value = displayedValue,
                label = ctx.getString(R.string.timeout),
                minValue = 0,
                maxValue = Int.MAX_VALUE,
                keyboardType = KeyboardType.Number,
                modifier = Modifier
                    .weight(1f)
                    .background(
                        color = MaterialTheme.colorScheme.surface.adjustBrightness(if (enabled) 1f else 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                scope = scope,
                enabled = enabled
            ) { newValue ->
                val parsed = newValue.toIntOrNull() ?: return@SettingsOutlinedField
                val seconds = parsed * unitMultiplier
                scope.launch {
                    LockSettingsStore.updateLockSettings(
                        ctx,
                        settings.copy(lockTimeoutSeconds = seconds)
                    )
                }
            }

            ActionSelectorRow(
                options = TimeoutOptions.entries,
                selected = selectedUnit,
                enabled = enabled,
                backgroundColor = MaterialTheme.colorScheme.secondary,
                surfaceColor = MaterialTheme.colorScheme.surface,
                textColor = MaterialTheme.colorScheme.onSecondary,
                optionLabel = { it.name.lowercase().replaceFirstChar { c -> c.uppercase() } }
            ) { newOption ->
                scope.launch{ LockSettingsStore.setUnitSelected(ctx, newOption) }
            }
        }
    }
}
