package org.elnix.notes.data.settings.stores

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
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

    private val SHOW_USER_VALIDATION_EDIT_MULTIPLE_NOTE = booleanPreferencesKey("show_user_validation_edit_multiple_note")
    fun getShowUserValidationEditMultipleNote(ctx: Context): Flow<Boolean> =
        ctx.dataStore.data.map { it[SHOW_USER_VALIDATION_EDIT_MULTIPLE_NOTE] ?: true }
    suspend fun setShowUserValidationEditMultipleNote(ctx: Context, enabled: Boolean) {
        ctx.dataStore.edit { it[SHOW_USER_VALIDATION_EDIT_MULTIPLE_NOTE] = enabled }
    }

    private val SHOW_ENABLE_DEBUG_ = booleanPreferencesKey("show_enable_debug")
    fun getShowEnableDebug(ctx: Context): Flow<Boolean> =
        ctx.dataStore.data.map { it[SHOW_ENABLE_DEBUG_] ?: true }
    suspend fun setShowEnableDebug(ctx: Context, enabled: Boolean) {
        ctx.dataStore.edit { it[SHOW_ENABLE_DEBUG_] = enabled }
    }

    suspend fun resetAll(ctx: Context) {
        ctx.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}