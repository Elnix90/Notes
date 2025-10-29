package org.elnix.notes.data.settings.stores

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.elnix.notes.data.settings.dataStore
import org.elnix.notes.utils.ReminderOffset
import org.json.JSONArray
import org.json.JSONObject
import kotlin.collections.forEach

object ReminderSettingsStore {
    private val DEFAULT_REMINDERS = stringPreferencesKey("default_reminders")

    fun getDefaultRemindersFlow(ctx: Context): Flow<List<ReminderOffset>> =
        ctx.dataStore.data.map { prefs ->
            prefs[DEFAULT_REMINDERS]?.let { jsonStr ->
                val arr = JSONArray(jsonStr)
                List(arr.length()) { i ->
                    val obj = arr.getJSONObject(i)
                    ReminderOffset(
                        minutesFromNow = if (obj.has("minutes")) obj.getLong("minutes") else null,
                        hourOfDay = if (obj.has("hour")) obj.getInt("hour") else null,
                        minute = if (obj.has("minute")) obj.getInt("minute") else null
                    )
                }
            } ?: emptyList()
        }

    suspend fun setDefaultReminders(ctx: Context, reminders: List<ReminderOffset>) {
        val jsonStr = JSONArray().apply {
            reminders.forEach { r ->
                put(JSONObject().apply {
                    r.minutesFromNow?.let { put("minutes", it) }
                    r.hourOfDay?.let { put("hour", it) }
                    r.minute?.let { put("minute", it) }
                })
            }
        }.toString()

        ctx.dataStore.edit { it[DEFAULT_REMINDERS] = jsonStr }
    }
}