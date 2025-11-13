package org.elnix.notes.data.settings.stores

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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
            prefs.remove(DEBUG_MODE_ENABLED)
        }
    }

    suspend fun getAll(ctx: Context): Map<String, Boolean> {
        val prefs = ctx.dataStore.data.first()
        return buildMap {
            prefs[DEBUG_MODE_ENABLED]?.let { put(DEBUG_MODE_ENABLED.name, it) }

        }
    }

    suspend fun setAll(ctx: Context, data: Map<String, Boolean>) {
        ctx.dataStore.edit { prefs ->
            data[DEBUG_MODE_ENABLED.name]?.let { prefs[DEBUG_MODE_ENABLED] = it }

        }
    }
}