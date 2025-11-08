package org.elnix.notes.ui.helpers

import android.content.Context
import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.elnix.notes.R
import org.elnix.notes.data.LockSettings
import org.elnix.notes.data.settings.stores.ColorSettingsStore
import org.elnix.notes.data.settings.stores.LockSettingsStore
import org.elnix.notes.ui.theme.AppObjectsColors
import org.json.JSONObject
import java.io.OutputStreamWriter

@Composable
fun ExportImportRow() {
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current

    // -------------------- IMPORT --------------------
    val importLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri == null) {
                Toast.makeText(ctx, ctx.getString(R.string.no_file_selected), Toast.LENGTH_SHORT).show()
                Log.w("ImportSettings", "User canceled file selection.")
                return@rememberLauncherForActivityResult
            }

            scope.launch {
                try {
                    val json = withContext(Dispatchers.IO) {
                        ctx.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                    }

                    if (json.isNullOrBlank()) {
                        Toast.makeText(ctx, ctx.getString(R.string.invalid_or_empty_file), Toast.LENGTH_SHORT).show()
                        Log.e("ImportSettings", "JSON content was empty or null.")
                        return@launch
                    }

                    Log.d("ImportSettings", "Loaded JSON: $json")
                    val obj = JSONObject(json)

                    // Apply color settings safely in IO context
                    withContext(Dispatchers.IO) {
                        suspend fun applyColor(key: String, setter: suspend (Context, Int) -> Unit) {
                            val value = obj.optInt(key)
                            if (value != 0) {
                                try {
                                    setter(ctx, value)
                                    Log.d("ImportSettings", "Applied color: $key = $value")
                                } catch (e: Exception) {
                                    Log.e("ImportSettings", "Failed to apply $key", e)
                                }
                            }
                        }

                        applyColor("primary", ColorSettingsStore::setPrimary)
                        applyColor("onPrimary", ColorSettingsStore::setOnPrimary)
                        applyColor("secondary", ColorSettingsStore::setSecondary)
                        applyColor("onSecondary", ColorSettingsStore::setOnSecondary)
                        applyColor("tertiary", ColorSettingsStore::setTertiary)
                        applyColor("onTertiary", ColorSettingsStore::setOnTertiary)
                        applyColor("background", ColorSettingsStore::setBackground)
                        applyColor("onBackground", ColorSettingsStore::setOnBackground)
                        applyColor("surface", ColorSettingsStore::setSurface)
                        applyColor("onSurface", ColorSettingsStore::setOnSurface)
                        applyColor("error", ColorSettingsStore::setError)
                        applyColor("onError", ColorSettingsStore::setOnError)
                        applyColor("outline", ColorSettingsStore::setOutline)
                        applyColor("delete", ColorSettingsStore::setDelete)
                        applyColor("edit", ColorSettingsStore::setEdit)
                        applyColor("complete", ColorSettingsStore::setComplete)



                        // Lock settings
                        if (obj.has("lock")) {
                            val lockObj = obj.getJSONObject("lock")
                            val settings = LockSettings(
                                useBiometrics = lockObj.optBoolean("useBiometrics", false),
                                useDeviceCredential = lockObj.optBoolean("useDeviceCredential", false),
                                lockTimeoutSeconds = lockObj.optInt("lockTimeoutSeconds", 300),
                                lastUnlockTimestamp = lockObj.optLong("lastUnlockTimestamp", 0L)
                            )
                            LockSettingsStore.updateLockSettings(ctx, settings)
                            Log.d("ImportSettings", "Updated lock settings.")
                        }
                    }

                    Toast.makeText(ctx, ctx.getString(R.string.settings_imported_successfully), Toast.LENGTH_LONG).show()
                    Log.i("ImportSettings", "Import completed successfully.")
                } catch (e: Exception) {
                    Log.e("ImportSettings", "Error during import", e)
                    Toast.makeText(ctx, "${ctx.getString(R.string.import_failed)}: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

    // -------------------- EXPORT --------------------
    val exportLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
            if (uri == null) {
                Toast.makeText(ctx, ctx.getString(R.string.export_cancelled), Toast.LENGTH_SHORT).show()
                Log.w("ExportSettings", "User canceled export.")
                return@rememberLauncherForActivityResult
            }

            scope.launch {
                try {
                    val json = withContext(Dispatchers.IO) {
                        val lock = LockSettingsStore.getLockSettings(ctx).first()

                        JSONObject().apply {
                            put("primary", ColorSettingsStore.getPrimary(ctx).first())
                            put("onPrimary", ColorSettingsStore.getOnPrimary(ctx).first())
                            put("secondary", ColorSettingsStore.getSecondary(ctx).first())
                            put("onSecondary", ColorSettingsStore.getOnSecondary(ctx).first())
                            put("tertiary", ColorSettingsStore.getTertiary(ctx).first())
                            put("onTertiary", ColorSettingsStore.getOnTertiary(ctx).first())
                            put("background", ColorSettingsStore.getBackground(ctx).first())
                            put("onBackground", ColorSettingsStore.getOnBackground(ctx).first())
                            put("surface", ColorSettingsStore.getSurface(ctx).first())
                            put("onSurface", ColorSettingsStore.getOnSurface(ctx).first())
                            put("error", ColorSettingsStore.getError(ctx).first())
                            put("onError", ColorSettingsStore.getOnError(ctx).first())
                            put("outline", ColorSettingsStore.getOutline(ctx).first())
                            put("delete", ColorSettingsStore.getDelete(ctx).first())
                            put("edit", ColorSettingsStore.getEdit(ctx).first())
                            put("complete", ColorSettingsStore.getComplete(ctx).first())
                            put("lock", JSONObject().apply {
                                put("useBiometrics", lock.useBiometrics)
                                put("useDeviceCredential", lock.useDeviceCredential)
                                put("lockTimeoutSeconds", lock.lockTimeoutSeconds)
                                put("lastUnlockTimestamp", lock.lastUnlockTimestamp)
                            })
                        }
                    }

                    Log.d("ExportSettings", "Generated JSON: $json")

                    withContext(Dispatchers.IO) {
                        ctx.contentResolver.openOutputStream(uri)?.use { output ->
                            OutputStreamWriter(output).use { it.write(json.toString(2)) }
                        }
                    }

                    Toast.makeText(ctx, ctx.getString(R.string.settings_exported_successfully), Toast.LENGTH_LONG).show()
                    Log.i("ExportSettings", "Export completed successfully.")
                } catch (e: Exception) {
                    Log.e("ExportSettings", "Error during export", e)
                    Toast.makeText(ctx, "${ctx.getString(R.string.export_failed)}: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

    // -------------------- UI ROW --------------------
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = {
                try {
                    exportLauncher.launch("notes_settings_backup.json")
                } catch (e: Exception) {
                    Log.e("ExportButton", "Crash on export launch", e)
                    Toast.makeText(ctx, "Export failed to start: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            colors = AppObjectsColors.buttonColors()
        ) {
            Text(stringResource(R.string.export_settings))
        }

        Button(
            onClick = {
                try {
                    importLauncher.launch(arrayOf("application/json"))
                } catch (e: Exception) {
                    Log.e("ImportButton", "Crash on import launch", e)
                    Toast.makeText(ctx, "Import failed to start: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            colors = AppObjectsColors.buttonColors()
        ) {
            Text(stringResource(R.string.import_settings))
        }
    }
}
