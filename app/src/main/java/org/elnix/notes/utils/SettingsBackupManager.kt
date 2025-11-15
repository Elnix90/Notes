package org.elnix.notes.utils

import android.content.Context
import android.net.Uri
import android.util.Log
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
import org.elnix.notes.data.settings.stores.TagsSettingsStore
import org.elnix.notes.data.settings.stores.ToolbarItemsSettingsStore
import org.elnix.notes.data.settings.stores.ToolbarsSettingsStore
import org.elnix.notes.data.settings.stores.UiSettingsStore
import org.elnix.notes.data.settings.stores.UserConfirmSettingsStore
import org.json.JSONObject
import java.io.OutputStreamWriter

object SettingsBackupManager {

    private const val TAG = "SettingsBackupManager"

    suspend fun exportSettings(ctx: Context, uri: Uri) {
        try {


            val json = JSONObject().apply {
                put("actions", mapStringToJson(ActionSettingsStore.getAll(ctx)))
                put("color_mode", mapStringToJson(ColorModesSettingsStore.getAll(ctx)))
                put("color", mapIntToJson(ColorSettingsStore.getAll(ctx)))
                put("debug", mapToJson(DebugSettingsStore.getAll(ctx)))
                put("language", mapStringToJson(LanguageSettingsStore.getAll(ctx)))
                put("lock", mapStringToJson(LockSettingsStore.getAll(ctx)))
                put("notifications", NotificationsSettingsStore.getAll(ctx))
                put("offsets", mapStringToJson(OffsetsSettingsStore.getAll(ctx)))
                put("plugins", mapToJson(PluginsSettingsStore.getAll(ctx)))
                put("reminders", mapStringToJson(ReminderSettingsStore.getAll(ctx)))
                put("tags", mapStringToJson(TagsSettingsStore.getAll(ctx)))
                put("toolbar_items", mapStringToJson(ToolbarItemsSettingsStore.getAll(ctx)))
                put("toolbars", mapStringToJson(ToolbarsSettingsStore.getAll(ctx)))
                put("ui", mapToJson(UiSettingsStore.getAll(ctx)))
                put("user_confirm", mapToJson(UserConfirmSettingsStore.getAll(ctx)))
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

    suspend fun importSettings(ctx: Context, uri: Uri) {
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

            withContext(Dispatchers.IO) {
                obj.optJSONObject("actions")?.let { ActionSettingsStore.setAll(ctx, jsonToMapString(it)) }
                obj.optJSONObject("color_mode")?.let { ColorModesSettingsStore.setAll(ctx, jsonToMapString(it)) }
                obj.optJSONObject("color")?.let { ColorSettingsStore.setAll(ctx, jsonToMapInt(it)) }
                obj.optJSONObject("debug")?.let { DebugSettingsStore.setAll(ctx, jsonToMap(it)) }
                obj.optJSONObject("language")?.let { LanguageSettingsStore.setAll(ctx, jsonToMapString(it)) }
                obj.optJSONObject("lock")?.let { LockSettingsStore.setAll(ctx, jsonToMapString(it)) }
                obj.optJSONObject("notifications")?.let { notifJson ->
                    NotificationsSettingsStore.setAll(ctx, notifJson.toString())
                }

                obj.optJSONObject("offsets")?.let { OffsetsSettingsStore.setAll(ctx, jsonToMapString(it)) }
                obj.optJSONObject("plugins")?.let { PluginsSettingsStore.setAll(ctx, jsonToMap(it)) }
                obj.optJSONObject("reminders")?.let { ReminderSettingsStore.setAll(ctx, jsonToMapString(it)) }
                obj.optJSONObject("tags")?.let { TagsSettingsStore.setAll(ctx, jsonToMapString(it)) }
                obj.optJSONObject("toolbar_items")?.let { ToolbarItemsSettingsStore.setAll(ctx, jsonToMapString(it)) }
                obj.optJSONObject("toolbars")?.let { ToolbarsSettingsStore.setAll(ctx, jsonToMapString(it)) }
                obj.optJSONObject("ui")?.let { UiSettingsStore.setAll(ctx, jsonToMap(it)) }
                obj.optJSONObject("user_confirm")?.let { UserConfirmSettingsStore.setAll(ctx, jsonToMap(it)) }
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
