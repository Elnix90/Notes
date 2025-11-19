package org.elnix.notes.data.settings.stores

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.elnix.notes.data.settings.dataStore
import org.elnix.notes.utils.ReminderOffset
import org.json.JSONArray
import org.json.JSONObject

object ReminderSettingsStore {

    /* ------------------------------
       KEYS
    ------------------------------ */

    private val REMINDERS_KEY = stringPreferencesKey("app_reminders")
    private val DEFAULT_REMINDERS_KEY = stringPreferencesKey("default_reminders")


    /* ------------------------------
       PUBLIC FLOWS
    ------------------------------ */

    fun getReminders(ctx: Context): Flow<List<ReminderOffset>> =
        ctx.dataStore.data.map { prefs ->
            prefs[REMINDERS_KEY]?.let { jsonStr ->
                parseReminderList(jsonStr)
            } ?: emptyList()
        }

    fun getDefaultRemindersFlow(ctx: Context): Flow<List<ReminderOffset>> =
        ctx.dataStore.data.map { prefs ->
            prefs[DEFAULT_REMINDERS_KEY]?.let { jsonStr ->
                parseReminderList(jsonStr)
            } ?: emptyList()
        }


    /* ------------------------------
       PRIVATE JSON PARSE / SAVE
    ------------------------------ */

    private fun parseReminderList(jsonStr: String): List<ReminderOffset> {
        val arr = JSONArray(jsonStr)
        return List(arr.length()) { i ->
            val obj = arr.getJSONObject(i)
            ReminderOffset(
                secondsFromNow = obj.optLong("seconds").takeIf { obj.has("seconds") },
                absoluteTimeMillis = obj.optLong("absolute").takeIf { obj.has("absolute") }
            )
        }
    }

    private suspend fun saveReminderList(ctx: Context, items: List<ReminderOffset>) {
        val jsonStr = JSONArray().apply {
            items.forEach { r ->
                put(JSONObject().apply {
                    r.secondsFromNow?.let { put("seconds", it) }
                    r.absoluteTimeMillis?.let { put("absolute", it) }
                })
            }
        }.toString()

        ctx.dataStore.edit { it[REMINDERS_KEY] = jsonStr }
    }

    private suspend fun saveDefaultReminderList(ctx: Context, items: List<ReminderOffset>) {
        val jsonStr = JSONArray().apply {
            items.forEach { r ->
                put(JSONObject().apply {
                    r.secondsFromNow?.let { put("seconds", it) }
                    r.absoluteTimeMillis?.let { put("absolute", it) }
                })
            }
        }.toString()

        ctx.dataStore.edit { it[DEFAULT_REMINDERS_KEY] = jsonStr }
    }


    /* ------------------------------
       DEFAULTS WRITE
    ------------------------------ */

    suspend fun setDefaultReminders(ctx: Context, list: List<ReminderOffset>) {
        saveDefaultReminderList(ctx, list)
    }


    /* ------------------------------
       GET ONCE (SUSPEND)
    ------------------------------ */

    private suspend fun getRemindersOnce(ctx: Context): MutableList<ReminderOffset> =
        getReminders(ctx).first().toMutableList()


    /* ------------------------------
       CRUD OPERATIONS
    ------------------------------ */

    suspend fun addReminder(ctx: Context, r: ReminderOffset) {
        val list = getRemindersOnce(ctx)
        list.add(r)
        saveReminderList(ctx, list)
    }

    suspend fun updateReminder(ctx: Context, original: ReminderOffset, updated: ReminderOffset) {
        val list = getRemindersOnce(ctx)
        val index = list.indexOf(original)
        if (index != -1) {
            list[index] = updated
            saveReminderList(ctx, list)
        }
    }

    suspend fun deleteReminder(ctx: Context, r: ReminderOffset) {
        val list = getRemindersOnce(ctx)
        list.remove(r)
        saveReminderList(ctx, list)
    }


    /* ------------------------------
       RESET / IMPORT / EXPORT
    ------------------------------ */

    suspend fun resetAll(ctx: Context) {
        ctx.dataStore.edit { prefs ->
            prefs.remove(REMINDERS_KEY)
            prefs.remove(DEFAULT_REMINDERS_KEY)
        }
    }

    suspend fun getAll(ctx: Context): Map<String, String> {
        val prefs = ctx.dataStore.data.first()
        return buildMap {
            prefs[REMINDERS_KEY]?.let { put(REMINDERS_KEY.name, it) }
            prefs[DEFAULT_REMINDERS_KEY]?.let { put(DEFAULT_REMINDERS_KEY.name, it) }
        }
    }

    suspend fun setAll(ctx: Context, data: Map<String, String>) {
        ctx.dataStore.edit { prefs ->
            data[REMINDERS_KEY.name]?.let { prefs[REMINDERS_KEY] = it }
            data[DEFAULT_REMINDERS_KEY.name]?.let { prefs[DEFAULT_REMINDERS_KEY] = it }
        }
    }
}
