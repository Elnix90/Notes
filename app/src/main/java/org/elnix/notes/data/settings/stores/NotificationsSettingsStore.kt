package org.elnix.notes.data.settings.stores

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.elnix.notes.R
import org.elnix.notes.data.settings.dataStore

enum class NotificationActionType { MARK_COMPLETED, SNOOZE, DELETE }

fun notificationActionName(ctx: Context, notifAction: NotificationActionType) = when (notifAction) {
    NotificationActionType.MARK_COMPLETED -> ctx.getString(R.string.complete)
    NotificationActionType.SNOOZE -> ctx.getString(R.string.snooze)
    NotificationActionType.DELETE -> ctx.getString(R.string.delete)
}

data class NotificationActionSetting(
    val actionType: NotificationActionType,
    val enabled: Boolean = true,
    val snoozeMinutes: Int = 10
)

object NotificationsSettingsStore {

    private val KEY = stringPreferencesKey("notification_actions")
    private val gson = Gson()

    private val listType = object : TypeToken<List<NotificationActionSetting>>() {}.type

    val defaultList = listOf(
        NotificationActionSetting(NotificationActionType.MARK_COMPLETED, enabled = true),
        NotificationActionSetting(NotificationActionType.SNOOZE, enabled = true, snoozeMinutes = 10),
        NotificationActionSetting(NotificationActionType.DELETE, enabled = true)
    )

    // -------------------------------------------------------------------------
    // Flow (full list, including order)
    // -------------------------------------------------------------------------
    fun getSettingsFlow(ctx: Context): Flow<List<NotificationActionSetting>> =
        ctx.dataStore.data.map { prefs ->
            val raw = prefs[KEY]
            if (raw.isNullOrBlank()) {
                defaultList
            } else {
                runCatching { gson.fromJson<List<NotificationActionSetting>>(raw, listType) }
                    .getOrDefault(defaultList)
            }
        }

    // -------------------------------------------------------------------------
    // Enable/Disable a specific action
    // -------------------------------------------------------------------------
    suspend fun setEnabled(ctx: Context, type: NotificationActionType, enabled: Boolean) {
        update(ctx) { list ->
            list.map {
                if (it.actionType == type) it.copy(enabled = enabled)
                else it
            }
        }
    }

    // -------------------------------------------------------------------------
    // Update snooze minutes
    // -------------------------------------------------------------------------
    suspend fun setSnoozeDuration(ctx: Context, minutes: Int) {
        update(ctx) { list ->
            list.map {
                if (it.actionType == NotificationActionType.SNOOZE) it.copy(snoozeMinutes = minutes)
                else it
            }
        }
    }

    suspend fun getSnoozeDuration(ctx: Context): Int {
        val current = getList(ctx)
        return current.find { it.actionType == NotificationActionType.SNOOZE }?.snoozeMinutes ?: 10
    }

    // -------------------------------------------------------------------------
    // Reorder: replace list in new order
    // -------------------------------------------------------------------------
    suspend fun setActionOrder(ctx: Context, order: List<NotificationActionType>) {
        update(ctx) { list ->
            val map = list.associateBy { it.actionType }
            order.mapNotNull { map[it] }
        }
    }

    // -------------------------------------------------------------------------
    // Get the list (one-shot)
    // -------------------------------------------------------------------------
    suspend fun getList(ctx: Context): List<NotificationActionSetting> {
        val raw = ctx.dataStore.data.first()[KEY]
        if (raw.isNullOrBlank()) return defaultList
        return runCatching { gson.fromJson<List<NotificationActionSetting>>(raw, listType) }
            .getOrDefault(defaultList)
    }

    // -------------------------------------------------------------------------
    // Internal update helper (mirrors ToolbarsSettingsStore.updateToolbarSetting)
    // -------------------------------------------------------------------------
    private suspend fun update(
        ctx: Context,
        modifier: (List<NotificationActionSetting>) -> List<NotificationActionSetting>
    ) {
        withContext(Dispatchers.IO) {
            ctx.dataStore.edit { prefs ->
                val raw = prefs[KEY]
                val current = if (raw.isNullOrBlank()) {
                    defaultList
                } else {
                    runCatching { gson.fromJson<List<NotificationActionSetting>>(raw, listType) }
                        .getOrDefault(defaultList)
                }

                val updated = modifier(current)
                prefs[KEY] = gson.toJson(updated)
            }
        }
    }


    private val CLICK_ON_NOTIFICATION_TO_OPEN_NOTE = booleanPreferencesKey("click_on_notification_to_open_note")
    fun getClickOnNotificationToOpenNote(ctx: Context): Flow<Boolean> =
        ctx.dataStore.data.map { it[CLICK_ON_NOTIFICATION_TO_OPEN_NOTE] ?: true }
    suspend fun setClickOnNotificationToOpenNote(ctx: Context, enabled: Boolean) {
        ctx.dataStore.edit { it[CLICK_ON_NOTIFICATION_TO_OPEN_NOTE] = enabled }
    }


    // -------------------------------------------------------------------------
    // Reset + Backup/Restore
    // -------------------------------------------------------------------------
    suspend fun resetAll(ctx: Context) {
        ctx.dataStore.edit { prefs -> prefs.remove(KEY) }
    }

    suspend fun getAll(ctx: Context): Map<String, String> {
        val prefs = ctx.dataStore.data.first()
        return buildMap {
            prefs[KEY]?.let { json ->
                put(KEY.name, json)
            }
        }
    }

    suspend fun setAll(ctx: Context, data: Map<String, String>) {
        ctx.dataStore.edit { prefs ->
            data[KEY.name]?.let {
                prefs[KEY] = it
            }
        }
    }
}
