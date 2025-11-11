package org.elnix.notes.data.settings.stores

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.elnix.notes.data.settings.dataStore

object DebugSettingsStore {
    private val DEBUG_MODE_ENABLED = booleanPreferencesKey("debug_mode_enabled")
    fun getDebugMode(ctx: Context): Flow<Boolean> = ctx.dataStore.data.map { it[DEBUG_MODE_ENABLED] ?: false }

    suspend fun setDebugMode(ctx: Context, state: Boolean) {
        ctx.dataStore.edit { it[DEBUG_MODE_ENABLED] = state}
    }

    suspend fun resetAll(ctx: Context) {
        ctx.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}