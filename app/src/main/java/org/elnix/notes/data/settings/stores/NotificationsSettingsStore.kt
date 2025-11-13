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

data class NotificationsBackup(
    val markCompletedEnabled: Boolean,
    val snoozeEnabled: Boolean,
    val deleteEnabled: Boolean,
    val snoozeDuration: Int
)


object NotificationsSettingsStore {

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

    suspend fun getAll(ctx: Context): NotificationsBackup {
        val prefs = ctx.dataStore.data.first()
        return NotificationsBackup(
            markCompletedEnabled = prefs[MARK_COMPLETED_ENABLED] ?: true,
            snoozeEnabled = prefs[SNOOZE_ENABLED] ?: true,
            deleteEnabled = prefs[DELETE_ENABLED] ?: true,
            snoozeDuration = prefs[SNOOZE_DURATION] ?: 10
        )
    }


    suspend fun setAll(ctx: Context, backup: NotificationsBackup) {
        ctx.dataStore.edit { prefs ->
            prefs[MARK_COMPLETED_ENABLED] = backup.markCompletedEnabled
            prefs[SNOOZE_ENABLED] = backup.snoozeEnabled
            prefs[DELETE_ENABLED] = backup.deleteEnabled
            prefs[SNOOZE_DURATION] = backup.snoozeDuration
        }
    }
}
