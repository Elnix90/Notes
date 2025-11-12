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

object ReminderSettingsStore {
    private val DEFAULT_REMINDERS = stringPreferencesKey("default_reminders")

    fun getDefaultRemindersFlow(ctx: Context): Flow<List<ReminderOffset>> =
        ctx.dataStore.data.map { prefs ->
            prefs[DEFAULT_REMINDERS]?.let { jsonStr ->
                val arr = JSONArray(jsonStr)
                List(arr.length()) { i ->
                    val obj = arr.getJSONObject(i)
                    ReminderOffset(
                        secondsFromNow = if (obj.has("seconds")) obj.getLong("seconds") else 0,
                    )
                }
            } ?: emptyList()
        }

    suspend fun setDefaultReminders(ctx: Context, reminders: List<ReminderOffset>) {
        val jsonStr = JSONArray().apply {
            reminders.forEach { r ->
                put(JSONObject().apply {
                    put("seconds", r.secondsFromNow)
                })
            }
        }.toString()

        ctx.dataStore.edit { it[DEFAULT_REMINDERS] = jsonStr }
    }

    suspend fun resetAll(ctx: Context) {
        ctx.dataStore.edit { prefs ->
            prefs.remove(DEFAULT_REMINDERS)
        }
    }
}