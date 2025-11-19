package org.elnix.notes.data.settings.stores

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.elnix.notes.data.settings.dataStore

/** Strongly-typed entries for user confirmation settings */
object UserConfirmEntry {

    val SHOW_USER_VALIDATION_DELETE_NOTE = UserConfirmSetting("show_user_validation_delete_note", true)
    val SHOW_USER_VALIDATION_MULTIPLE_DELETE_NOTE = UserConfirmSetting("show_user_validation_multiple_delete_note", true)
    val SHOW_ENABLE_DEBUG = UserConfirmSetting("show_enable_debug", true)
    val SHOW_USER_VALIDATION_DELETE_OFFSET = UserConfirmSetting("show_user_validation_delete_offset", true)
    val SHOW_USER_VALIDATION_DELETE_TAG = UserConfirmSetting("show_user_validation_delete_tag", true)


    /** All entries in a list for automatic operations */
    val allEntries = listOf(
        SHOW_USER_VALIDATION_DELETE_NOTE,
        SHOW_USER_VALIDATION_MULTIPLE_DELETE_NOTE,
        SHOW_ENABLE_DEBUG,
        SHOW_USER_VALIDATION_DELETE_OFFSET,
        SHOW_USER_VALIDATION_DELETE_TAG
    )
}

/** Represents a single setting entry */
data class UserConfirmSetting(
    val name: String,
    val default: Boolean
) {
    val prefKey = booleanPreferencesKey(name)
}

/** The settings store */
object UserConfirmSettingsStore {

    /** Generic flow getter */
    fun get(ctx: Context, entry: UserConfirmSetting): Flow<Boolean> =
        ctx.dataStore.data.map { it[entry.prefKey] ?: entry.default }

    /** Generic setter */
    suspend fun set(ctx: Context, entry: UserConfirmSetting, value: Boolean) {
        ctx.dataStore.edit { it[entry.prefKey] = value }
    }

    /** Reset all settings */
    suspend fun resetAll(ctx: Context) {
        ctx.dataStore.edit { prefs ->
            UserConfirmEntry.allEntries.forEach { prefs.remove(it.prefKey) }
        }
    }

    /** Export all settings as a map */
    suspend fun getAll(ctx: Context): Map<String, Boolean> {
        val prefs = ctx.dataStore.data.first()
        return UserConfirmEntry.allEntries.associate { it.name to (prefs[it.prefKey] ?: it.default) }
    }

    /** Import settings from a map */
    suspend fun setAll(ctx: Context, data: Map<String, Boolean>) {
        ctx.dataStore.edit { prefs ->
            data.forEach { (name, value) ->
                UserConfirmEntry.allEntries.find { it.name == name }?.let { prefs[it.prefKey] = value }
            }
        }
    }
}
