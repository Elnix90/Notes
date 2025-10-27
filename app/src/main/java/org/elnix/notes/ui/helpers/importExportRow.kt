package org.elnix.notes.ui.helpers

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.elnix.notes.data.settings.ColorSettingsStore
import org.elnix.notes.data.settings.LockSettingsStore
import org.elnix.notes.data.settings.ShowNavBarActions
import org.elnix.notes.data.settings.UiSettingsStore
import org.elnix.notes.ui.theme.AppObjectsColors
import org.json.JSONObject
import java.io.OutputStreamWriter

@Composable
fun ExportImportRow(
    ctx: Context,
    primaryColor: Int,
    backgroundColor: Int,
    onBackgroundColor: Int,
    showNavBarLabels: ShowNavBarActions
) {
    val scope = rememberCoroutineScope()

    // --- Import launcher ---
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            ctx.contentResolver.openInputStream(uri)?.use { input ->
                val json = input.bufferedReader().readText()
                val obj = JSONObject(json)

                scope.launch {
                    // --- Color settings ---
                    obj.optInt("primary").takeIf { it != 0 }?.let { ColorSettingsStore.setPrimary(ctx, it) }
                    obj.optInt("background").takeIf { it != 0 }?.let { ColorSettingsStore.setBackground(ctx, it) }
                    obj.optInt("onBackground").takeIf { it != 0 }?.let { ColorSettingsStore.setOnBackground(ctx, it) }

                    // --- UI ---
                    obj.optString("showNavLabels").takeIf { it.isNotBlank() }?.let {
                        UiSettingsStore.setShowBottomNavLabelsFlow(ctx, ShowNavBarActions.valueOf(it))
                    }

                    // --- Lock settings ---
                    if (obj.has("lock")) {
                        val lockObj = obj.getJSONObject("lock")
                        val settings = org.elnix.notes.data.LockSettings(
                            useBiometrics = lockObj.optBoolean("useBiometrics", false),
                            useDeviceCredential = lockObj.optBoolean("useDeviceCredential", false),
                            lockTimeoutSeconds = lockObj.optInt("lockTimeoutSeconds", 300),
                            lastUnlockTimestamp = lockObj.optLong("lastUnlockTimestamp", 0L)
                        )
                        LockSettingsStore.updateLockSettings(ctx, settings)
                    }
                }
            }
        }
    }

    // --- Export launcher ---
    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri != null) {
            scope.launch {
                val json = JSONObject().apply {
                    put("primary", primaryColor)
                    put("background", backgroundColor)
                    put("onBackground", onBackgroundColor)
                    put("showNavLabels", showNavBarLabels.name)
                    put("lock", JSONObject().apply {
                        val lock = LockSettingsStore.getLockSettings(ctx)
                        lock.collect { lockSettings ->
                            put("useBiometrics", lockSettings.useBiometrics)
                            put("useDeviceCredential", lockSettings.useDeviceCredential)
                            put("lockTimeoutSeconds", lockSettings.lockTimeoutSeconds)
                            put("lastUnlockTimestamp", lockSettings.lastUnlockTimestamp)
                        }
                    })
                }

                ctx.contentResolver.openOutputStream(uri)?.use { output ->
                    OutputStreamWriter(output).use { it.write(json.toString(2)) }
                }
            }
        }
    }

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = { exportLauncher.launch("settings_backup.json") },
            colors = AppObjectsColors.buttonColors()
        ) {
            Text("Export Settings")
        }

        Button(
            onClick = { importLauncher.launch(arrayOf("application/json")) },
            colors = AppObjectsColors.buttonColors()
        ) {
            Text("Import Settings")
        }
    }
}
