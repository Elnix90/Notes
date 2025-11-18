package org.elnix.notes.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.elnix.notes.data.settings.stores.ActionSettingsStore
import org.elnix.notes.data.settings.stores.ColorModesSettingsStore
import org.elnix.notes.data.settings.stores.ColorSettingsStore
import org.elnix.notes.data.settings.stores.DebugSettingsStore
import org.elnix.notes.data.settings.stores.LanguageSettingsStore
import org.elnix.notes.data.settings.stores.LockSettingsStore
import org.elnix.notes.data.settings.stores.NotificationsSettingsStore
import org.elnix.notes.data.settings.stores.OffsetsSettingsStore
import org.elnix.notes.data.settings.stores.PluginsSettingsStore
import org.elnix.notes.data.settings.stores.ReminderSettingsStore
import org.elnix.notes.data.settings.stores.SortSettingsStore
import org.elnix.notes.data.settings.stores.TagsSettingsStore
import org.elnix.notes.data.settings.stores.ToolbarItemsSettingsStore
import org.elnix.notes.data.settings.stores.ToolbarsSettingsStore
import org.elnix.notes.data.settings.stores.UiSettingsStore
import org.elnix.notes.data.settings.stores.UserConfirmSettingsStore
import org.elnix.notes.security.requireBiometricAuth
import org.json.JSONObject
import java.io.OutputStreamWriter

object SettingsBackupManager {

    private const val TAG = "SettingsBackupManager"

    suspend fun exportSettings(ctx: Context, uri: Uri) {
        try {


            val json = JSONObject().apply {

                fun putIfNotEmpty(key: String, obj: JSONObject) {
                    if (obj.length() > 0) put(key, obj)
                }

                putIfNotEmpty("actions", mapStringToJson(ActionSettingsStore.getAll(ctx)))
                putIfNotEmpty("color_mode", mapStringToJson(ColorModesSettingsStore.getAll(ctx)))
                putIfNotEmpty("color", mapIntToJson(ColorSettingsStore.getAll(ctx)))
                putIfNotEmpty("debug", mapToJson(DebugSettingsStore.getAll(ctx)))
                putIfNotEmpty("language", mapStringToJson(LanguageSettingsStore.getAll(ctx)))
                putIfNotEmpty("lock", mapStringToJson(LockSettingsStore.getAll(ctx)))
                putIfNotEmpty("notifications", mapStringToJson(NotificationsSettingsStore.getAll(ctx)))
                putIfNotEmpty("offsets", mapStringToJson(OffsetsSettingsStore.getAll(ctx)))
                putIfNotEmpty("plugins", mapToJson(PluginsSettingsStore.getAll(ctx)))
                putIfNotEmpty("reminders", mapStringToJson(ReminderSettingsStore.getAll(ctx)))
                putIfNotEmpty("sort", mapStringToJson(SortSettingsStore.getAll(ctx)))
                putIfNotEmpty("tags", mapStringToJson(TagsSettingsStore.getAll(ctx)))
                putIfNotEmpty("toolbar_items", mapStringToJson(ToolbarItemsSettingsStore.getAll(ctx)))
                putIfNotEmpty("toolbars", mapStringToJson(ToolbarsSettingsStore.getAll(ctx)))
                putIfNotEmpty("ui", mapStringToJson(UiSettingsStore.getAll(ctx)))
                putIfNotEmpty("user_confirm", mapToJson(UserConfirmSettingsStore.getAll(ctx)))
            }


            Log.d(TAG, "Generated JSON: $json")

            withContext(Dispatchers.IO) {
                ctx.contentResolver.openOutputStream(uri)?.use { output ->
                    OutputStreamWriter(output).use { it.write(json.toString(2)) }
                }
            }

            Log.i(TAG, "Export completed successfully.")
        } catch (e: Exception) {
            Log.e(TAG, "Error during export", e)
            throw e
        }
    }

    suspend fun importSettings(ctx: Context, uri: Uri, activity: FragmentActivity) {
        try {
            val json = withContext(Dispatchers.IO) {
                ctx.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
            }

            if (json.isNullOrBlank()) {
                Log.e(TAG, "Invalid or empty file")
                throw IllegalArgumentException("Invalid or empty file")
            }

            Log.d(TAG, "Loaded JSON: $json")
            val obj = JSONObject(json)


            var biometricValidated = false
            suspend fun ensureBiometricOnce(usesBiometrics: Boolean, usesDeviceCredentials: Boolean): Boolean {
                if (!biometricValidated) {
                    biometricValidated = requireBiometricAuth(activity,usesBiometrics, usesDeviceCredentials)
                }
                return biometricValidated
            }

            withContext(Dispatchers.IO) {

                // ------------------ ACTIONS ------------------
                obj.optJSONObject("actions")?.let {
                    ActionSettingsStore.setAll(ctx, jsonToMapString(it))
                }

                // ------------------ COLOR MODE ------------------
                obj.optJSONObject("color_mode")?.let {
                    ColorModesSettingsStore.setAll(ctx, jsonToMapString(it))
                }

                // ------------------ COLOR ------------------
                obj.optJSONObject("color")?.let {
                    ColorSettingsStore.setAll(ctx, jsonToMapInt(it))
                }

                // ------------------ DEBUG ------------------
                obj.optJSONObject("debug")?.let {
                    DebugSettingsStore.setAll(ctx, jsonToMap(it))
                }

                // ------------------ LANGUAGE ------------------
                obj.optJSONObject("language")?.let {
                    LanguageSettingsStore.setAll(ctx, jsonToMapString(it))
                }

                // ------------------ LOCK SETTINGS (biometric required) ------------------
                obj.optJSONObject("lock")?.let { lockObj ->
                    val map = jsonToMapString(lockObj)

                    if (!ensureBiometricOnce(usesBiometrics = true, usesDeviceCredentials = false)) {
                        Log.w(TAG, "Biometric authentication failed for lock settings")
                        return@let
                    }

                    LockSettingsStore.setAll(ctx, map)
                }

                // ------------------ NOTIFICATIONS ------------------
                obj.optJSONObject("notifications")?.let {
                    NotificationsSettingsStore.setAll(ctx, jsonToMapString(it))
                }

                // ------------------ OFFSETS ------------------
                obj.optJSONObject("offsets")?.let {
                    OffsetsSettingsStore.setAll(ctx, jsonToMapString(it))
                }

                // ------------------ PLUGINS (biometric required if dangerous) ------------------
                obj.optJSONObject("plugins")?.let { pluginObj ->
                    val map = jsonToMap(pluginObj)

                    val biometricRequired = map["allow_alphallm_access"] == true

                    if (biometricRequired) {
                        if (!ensureBiometricOnce(
                                usesBiometrics = true,
                                usesDeviceCredentials = false
                            )) {
                            Log.w(TAG, "Biometric authentication failed for plugins")
                            return@let
                        }
                    }

                    PluginsSettingsStore.setAll(ctx, map)
                }

                // ------------------ REMINDERS ------------------
                obj.optJSONObject("reminders")?.let {
                    ReminderSettingsStore.setAll(ctx, jsonToMapString(it))
                }

                // ------------------ SORT ------------------
                obj.optJSONObject("sort")?.let {
                    SortSettingsStore.setAll(ctx, jsonToMapString(it))
                }

                // ------------------ TAGS ------------------
                obj.optJSONObject("tags")?.let {
                    TagsSettingsStore.setAll(ctx, jsonToMapString(it))
                }

                // ------------------ TOOLBAR ITEMS ------------------
                obj.optJSONObject("toolbar_items")?.let {
                    ToolbarItemsSettingsStore.setAll(ctx, jsonToMapString(it))
                }

                // ------------------ TOOLBARS ------------------
                obj.optJSONObject("toolbars")?.let {
                    ToolbarsSettingsStore.setAll(ctx, jsonToMapString(it))
                }

                // ------------------ UI ------------------
                obj.optJSONObject("ui")?.let {
                    UiSettingsStore.setAll(ctx, jsonToMapString(it))
                }

                // ------------------ USER CONFIRM ------------------
                obj.optJSONObject("user_confirm")?.let {
                    UserConfirmSettingsStore.setAll(ctx, jsonToMap(it))
                }
            }

            Log.i(TAG, "Import completed successfully.")
        } catch (e: Exception) {
            Log.e(TAG, "Error during import", e)
            throw e
        }
    }

    private fun mapToJson(map: Map<String, Boolean>) = JSONObject().apply {
        map.forEach { (key, value) -> put(key, value) }
    }

    private fun jsonToMap(obj: JSONObject) = buildMap {
        obj.keys().forEach { key -> put(key, obj.optBoolean(key, false)) }
    }

    private fun mapIntToJson(map: Map<String, Int>) = JSONObject().apply {
        map.forEach { (k, v) -> put(k, v) }
    }

    private fun jsonToMapInt(obj: JSONObject) = buildMap {
        obj.keys().forEach { key -> put(key, obj.optInt(key, 0)) }
    }

    private fun mapStringToJson(map: Map<String, String>) = JSONObject().apply {
        map.forEach { (key, value) -> put(key, value) }
    }

    private fun jsonToMapString(obj: JSONObject) = buildMap {
        obj.keys().forEach { key -> put(key, obj.optString(key, "")) }
    }
}
