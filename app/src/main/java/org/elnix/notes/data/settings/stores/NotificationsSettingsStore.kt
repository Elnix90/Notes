package org.elnix.notes.data.settings.stores

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.elnix.notes.data.settings.dataStore

enum class NotificationActionType { MARK_COMPLETED, SNOOZE, DELETE }

data class NotificationActionSetting(
    val enabled: Boolean = true,
    val actionType: NotificationActionType,
    val snoozeMinutes: Int = 10 // default snooze duration
)

object NotificationActionsStore {

    private val MARK_COMPLETED_ENABLED = booleanPreferencesKey("mark_completed_enabled")
    private val SNOOZE_ENABLED = booleanPreferencesKey("snooze_enabled")
    private val DELETE_ENABLED = booleanPreferencesKey("delete_enabled")
    private val SNOOZE_DURATION = intPreferencesKey("snooze_duration") // in minutes

    fun getSettingsFlow(ctx: Context): Flow<List<NotificationActionSetting>> =
        ctx.dataStore.data.map { prefs ->
            listOf(
                NotificationActionSetting(
                    enabled = prefs[MARK_COMPLETED_ENABLED] ?: true,
                    actionType = NotificationActionType.MARK_COMPLETED
                ),
                NotificationActionSetting(
                    enabled = prefs[SNOOZE_ENABLED] ?: true,
                    actionType = NotificationActionType.SNOOZE,
                    snoozeMinutes = prefs[SNOOZE_DURATION] ?: 10
                ),
                NotificationActionSetting(
                    enabled = prefs[DELETE_ENABLED] ?: true,
                    actionType = NotificationActionType.DELETE
                )
            )
        }

    suspend fun setEnabled(ctx: Context, actionType: NotificationActionType, enabled: Boolean) {
        ctx.dataStore.edit { prefs ->
            when(actionType) {
                NotificationActionType.MARK_COMPLETED -> prefs[MARK_COMPLETED_ENABLED] = enabled
                NotificationActionType.SNOOZE -> prefs[SNOOZE_ENABLED] = enabled
                NotificationActionType.DELETE -> prefs[DELETE_ENABLED] = enabled
            }
        }
    }

    suspend fun setSnoozeDuration(ctx: Context, minutes: Int) {
        ctx.dataStore.edit { prefs ->
            prefs[SNOOZE_DURATION] = minutes
        }
    }

    suspend fun getSnoozeDuration(ctx: Context): Int {
        val prefs = ctx.dataStore.data.first()
        return prefs[SNOOZE_DURATION] ?: 600
    }

    suspend fun resetAll(ctx: Context) {
        ctx.dataStore.edit { prefs ->
            prefs.remove(MARK_COMPLETED_ENABLED)
            prefs.remove(SNOOZE_ENABLED)
            prefs.remove(DELETE_ENABLED)
            prefs.remove(SNOOZE_DURATION)
        }
    }

    // --- Export all current settings as a map (Boolean + Int)
    suspend fun getAll(ctx: Context): Map<String, Any> {
        val prefs = ctx.dataStore.data.first()
        return mapOf(
            "mark_completed_enabled" to (prefs[MARK_COMPLETED_ENABLED] ?: true),
            "snooze_enabled" to (prefs[SNOOZE_ENABLED] ?: true),
            "delete_enabled" to (prefs[DELETE_ENABLED] ?: true),
            "snooze_duration" to (prefs[SNOOZE_DURATION] ?: 10)
        )
    }

    // --- Apply all settings from a backup map
    suspend fun setAll(ctx: Context, backup: Map<String, Any>) {
        ctx.dataStore.edit { prefs ->
            backup["mark_completed_enabled"]?.let { prefs[MARK_COMPLETED_ENABLED] = it as Boolean }
            backup["snooze_enabled"]?.let { prefs[SNOOZE_ENABLED] = it as Boolean }
            backup["delete_enabled"]?.let { prefs[DELETE_ENABLED] = it as Boolean }
            backup["snooze_duration"]?.let { prefs[SNOOZE_DURATION] = it as Int }
        }
    }
}
