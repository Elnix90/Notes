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
            prefs[REMINDERS_KEY]?.let(::parseReminderList) ?: emptyList()
        }

    fun getDefaultRemindersFlow(ctx: Context): Flow<List<ReminderOffset>> =
        ctx.dataStore.data.map { prefs ->
            prefs[DEFAULT_REMINDERS_KEY]?.let(::parseReminderList) ?: emptyList()
        }


    /* ------------------------------
       PRIVATE JSON PARSE / SAVE
    ------------------------------ */

    private fun parseReminderList(jsonStr: String): List<ReminderOffset> {
        val arr = JSONArray(jsonStr)

        return List(arr.length()) { index ->
            val obj = arr.getJSONObject(index)

            // Offset reminders
            val seconds =
                if (obj.has("seconds")) obj.getLong("seconds") else null

            // Absolute reminders
            val year = obj.optInt("year").takeIf { obj.has("year") }
            val month = obj.optInt("month").takeIf { obj.has("month") }
            val day = obj.optInt("day").takeIf { obj.has("day") }
            val hour = obj.optInt("hour").takeIf { obj.has("hour") }
            val minute = obj.optInt("minute").takeIf { obj.has("minute") }

            ReminderOffset(
                secondsFromNow = seconds,
                yearsFromToday = year,
                monthsFromToday = month,
                daysFromToday = day,
                hoursFromToday = hour,
                minutesFromToday = minute
            )
        }
    }

    private fun toJson(reminders: List<ReminderOffset>): String {
        val arr = JSONArray()

        reminders.forEach { r ->
            val obj = JSONObject()

            r.secondsFromNow?.let { obj.put("seconds", it) }

            r.yearsFromToday?.let { obj.put("year", it) }
            r.monthsFromToday?.let { obj.put("month", it) }
            r.daysFromToday?.let { obj.put("day", it) }
            r.hoursFromToday?.let { obj.put("hour", it) }
            r.minutesFromToday?.let { obj.put("minute", it) }

            arr.put(obj)
        }

        return arr.toString()
    }

    private suspend fun saveReminderList(ctx: Context, items: List<ReminderOffset>) {
        ctx.dataStore.edit { prefs ->
            prefs[REMINDERS_KEY] = toJson(items)
        }
    }

    private suspend fun saveDefaultReminderList(ctx: Context, items: List<ReminderOffset>) {
        ctx.dataStore.edit { prefs ->
            prefs[DEFAULT_REMINDERS_KEY] = toJson(items)
        }
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
