package org.elnix.notes.data.settings.stores

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.elnix.notes.data.settings.dataStore

object UserConfirmSettingsStore {

    private val SHOW_USER_VALIDATION_DELETE_NOTE = booleanPreferencesKey("show_user_validation_delete_note")
    fun getShowUserValidationDeleteNote(ctx: Context): Flow<Boolean> =
        ctx.dataStore.data.map { it[SHOW_USER_VALIDATION_DELETE_NOTE] ?: true }
    suspend fun setShowUserValidationDeleteNote(ctx: Context, enabled: Boolean) {
        ctx.dataStore.edit { it[SHOW_USER_VALIDATION_DELETE_NOTE] = enabled }
    }

    private val SHOW_USER_VALIDATION_MULTIPLE_DELETE_NOTE = booleanPreferencesKey("show_user_validation_multiple_delete_note")
    fun getShowUserValidationMultipleDeleteNote(ctx: Context): Flow<Boolean> =
        ctx.dataStore.data.map { it[SHOW_USER_VALIDATION_MULTIPLE_DELETE_NOTE] ?: true }
    suspend fun setShowUserValidationMultipleDeleteNote(ctx: Context, enabled: Boolean) {
        ctx.dataStore.edit { it[SHOW_USER_VALIDATION_MULTIPLE_DELETE_NOTE] = enabled }
    }

    private val SHOW_ENABLE_DEBUG = booleanPreferencesKey("show_enable_debug")
    fun getShowEnableDebug(ctx: Context): Flow<Boolean> =
        ctx.dataStore.data.map { it[SHOW_ENABLE_DEBUG] ?: true }
    suspend fun setShowEnableDebug(ctx: Context, enabled: Boolean) {
        ctx.dataStore.edit { it[SHOW_ENABLE_DEBUG] = enabled }
    }

    suspend fun resetAll(ctx: Context) {
        ctx.dataStore.edit { prefs ->
            prefs.remove(SHOW_ENABLE_DEBUG)
            prefs.remove(SHOW_USER_VALIDATION_DELETE_NOTE)
            prefs.remove(SHOW_USER_VALIDATION_MULTIPLE_DELETE_NOTE)
        }
    }

    suspend fun getAll(ctx: Context): Map<String, Boolean> {
        val prefs = ctx.dataStore.data.first()
        return buildMap {
            prefs[SHOW_USER_VALIDATION_DELETE_NOTE]?.let { put(SHOW_USER_VALIDATION_DELETE_NOTE.name, it) }
            prefs[SHOW_USER_VALIDATION_MULTIPLE_DELETE_NOTE]?.let { put(SHOW_USER_VALIDATION_MULTIPLE_DELETE_NOTE.name, it) }
            prefs[SHOW_ENABLE_DEBUG]?.let { put(SHOW_ENABLE_DEBUG.name, it) }
        }
    }

    suspend fun setAll(ctx: Context, data: Map<String, Boolean>) {
        ctx.dataStore.edit { prefs ->
            data[SHOW_USER_VALIDATION_DELETE_NOTE.name]?.let { prefs[SHOW_USER_VALIDATION_DELETE_NOTE] = it }
            data[SHOW_USER_VALIDATION_MULTIPLE_DELETE_NOTE.name]?.let { prefs[SHOW_USER_VALIDATION_MULTIPLE_DELETE_NOTE] = it }
            data[SHOW_ENABLE_DEBUG.name]?.let { prefs[SHOW_ENABLE_DEBUG] = it }
        }
    }

}