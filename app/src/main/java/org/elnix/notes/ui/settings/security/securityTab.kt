package org.elnix.notes.ui.settings.security

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.notes.data.LockSettings
import org.elnix.notes.data.settings.LockSettingsStore
import org.elnix.notes.security.BiometricManagerHelper
import org.elnix.notes.ui.helpers.SettingsOutlinedField
import org.elnix.notes.ui.helpers.SettingsTitle
import org.elnix.notes.ui.helpers.SwitchRow
import java.time.Instant

@Composable
fun SecurityTab(onBack: (() -> Unit)) {
    val ctx = LocalContext.current
    val activity = ctx as androidx.fragment.app.FragmentActivity
    val settings by LockSettingsStore.getLockSettings(ctx).collectAsState(initial = LockSettings())
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SettingsTitle("Security", onBack)


        SwitchRow(
            settings.useBiometrics,
            "Enable Biometrics",
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
            "Enable Device Credential",
        ) {
            scope.launch {
                LockSettingsStore.updateLockSettings(
                    ctx,
                    settings.copy(useDeviceCredential = it)
                )
            }
        }


        var selectedUnit by remember { mutableStateOf("Minutes") }
        var expanded by remember { mutableStateOf(false) }

        // Conversion multiplier to seconds
        val unitMultiplier = when (selectedUnit) {
            "Seconds" -> 1
            "Minutes" -> 60
            "Hours" -> 3600
            "Days" -> 86400
            else -> 60
        }

        // Convert current seconds back to the selected display unit
        val displayedValue = remember(settings.lockTimeoutSeconds, selectedUnit) {
            (settings.lockTimeoutSeconds / unitMultiplier).toString()
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Numeric input field
            SettingsOutlinedField(
                value = displayedValue,
                label = "Timeout",
                minValue = 0,
                maxValue = Int.MAX_VALUE,
                keyboardType = KeyboardType.Number,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                scope = scope
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

            // Dropdown for unit selection
            Box(
                modifier = Modifier
                    .wrapContentWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp)
            ) {
                TextButton(onClick = { expanded = true }) {
                    Text(selectedUnit, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = "Select unit",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    listOf("Seconds", "Minutes", "Hours", "Days").forEach { unit ->
                        DropdownMenuItem(
                            text = { Text(unit) },
                            onClick = {
                                selectedUnit = unit
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
