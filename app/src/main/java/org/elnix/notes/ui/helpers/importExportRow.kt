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
import org.elnix.notes.data.settings.stores.*
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

                    withContext(Dispatchers.IO) {


                        // -------------------- COLORS --------------------
                        val colorMap = mutableMapOf<String, Int>()
                        obj.keys().forEach { key ->
                            val value = obj.optInt(key, 0)
                            if (value != 0) colorMap[key] = value
                        }
                        ColorSettingsStore.setAll(ctx, colorMap)


                        // -------------------- LOCK --------------------
                        obj.optJSONObject("lock")?.let { lockObj ->
                            val lockSettings = LockSettings(
                                useBiometrics = lockObj.optBoolean("useBiometrics", false),
                                useDeviceCredential = lockObj.optBoolean("useDeviceCredential", false),
                                lockTimeoutSeconds = lockObj.optInt("lockTimeoutSeconds", 300),
                                lastUnlockTimestamp = lockObj.optLong("lastUnlockTimestamp", 0L)
                            )
                            LockSettingsStore.updateLockSettings(ctx, lockSettings)
                        }

                        // -------------------- UI SETTINGS --------------------
                        UiSettingsStore.setAllFromJson(ctx, obj.optJSONObject("ui") ?: JSONObject())

                        // -------------------- USER CONFIRM --------------------
                        UserConfirmSettingsStore.setAllFromJson(ctx, obj.optJSONObject("userConfirm") ?: JSONObject())

                        // -------------------- PLUGINS --------------------
                        PluginsSettingsStore.setAllFromJson(ctx, obj.optJSONObject("plugins") ?: JSONObject())
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
//                    val json = withContext(Dispatchers.IO) {
//
//                        // Build JSON with all settings using getAll
//                        JSONObject().apply {
//
//                            put("primary", ColorSettingsStore.getPrimary(ctx).first())
//                            put("onPrimary", ColorSettingsStore.getOnPrimary(ctx).first())
//                            put("secondary", ColorSettingsStore.getSecondary(ctx).first())
//                            put("onSecondary", ColorSettingsStore.getOnSecondary(ctx).first())
//                            put("tertiary", ColorSettingsStore.getTertiary(ctx).first())
//                            put("onTertiary", ColorSettingsStore.getOnTertiary(ctx).first())
//                            put("background", ColorSettingsStore.getBackground(ctx).first())
//                            put("onBackground", ColorSettingsStore.getOnBackground(ctx).first())
//                            put("surface", ColorSettingsStore.getSurface(ctx).first())
//                            put("onSurface", ColorSettingsStore.getOnSurface(ctx).first())
//                            put("error", ColorSettingsStore.getError(ctx).first())
//                            put("onError", ColorSettingsStore.getOnError(ctx).first())
//                            put("outline", ColorSettingsStore.getOutline(ctx).first())
//                            put("delete", ColorSettingsStore.getDelete(ctx).first())
//                            put("edit", ColorSettingsStore.getEdit(ctx).first())
//                            put("complete", ColorSettingsStore.getComplete(ctx).first())
//
//                            put("lock", JSONObject().apply {
//                                val lock = LockSettingsStore.getLockSettings(ctx).first()
//                                put("useBiometrics", lock.useBiometrics)
//                                put("useDeviceCredential", lock.useDeviceCredential)
//                                put("lockTimeoutSeconds", lock.lockTimeoutSeconds)
//                                put("lastUnlockTimestamp", lock.lastUnlockTimestamp)
//                            })
//
//                            put("ui", JSONObject(UiSettingsStore.getAll(ctx)))
//                            put("userConfirm", JSONObject(UserConfirmSettingsStore.getAll(ctx)))
//                            put("plugins", JSONObject(PluginsSettingsStore.getAll(ctx)))
//                        }
//                    }

                    val json = JSONObject().apply {
                        put("colors", mapIntToJson(ColorSettingsStore.getAll(ctx)))
                        put("ui", mapToJson(UiSettingsStore.getAll(ctx)))
                        put("userConfirm", mapToJson(UserConfirmSettingsStore.getAll(ctx)))
                        put("plugins", mapToJson(PluginsSettingsStore.getAll(ctx)))

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
            onClick = { exportLauncher.launch("notes_settings_backup.json") },
            colors = AppObjectsColors.buttonColors()
        ) { Text(stringResource(R.string.export_settings)) }

        Button(
            onClick = { importLauncher.launch(arrayOf("application/json")) },
            colors = AppObjectsColors.buttonColors()
        ) { Text(stringResource(R.string.import_settings)) }
    }
}


    // Convert a Map<String, Boolean> to JSONObject
    fun mapToJson(map: Map<String, Boolean>): JSONObject {
        return JSONObject().apply {
            map.forEach { (key, value) -> put(key, value) }
        }
    }

    // Convert JSONObject to Map<String, Boolean>
    fun jsonToMap(obj: JSONObject): Map<String, Boolean> {
        return buildMap {
            obj.keys().forEach { key ->
                put(key, obj.optBoolean(key, false))
            }
        }
    }

    // Similarly, for Map<String, Int>
    fun mapIntToJson(map: Map<String, Int>): JSONObject {
        return JSONObject().apply { map.forEach { (k, v) -> put(k, v) } }
    }

    fun jsonToMapInt(obj: JSONObject): Map<String, Int> {
        return buildMap {
            obj.keys().forEach { key ->
                put(key, obj.optInt(key, 0))
            }
        }
    }
