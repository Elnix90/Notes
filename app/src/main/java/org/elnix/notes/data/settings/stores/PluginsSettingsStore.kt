package org.elnix.notes.data.settings.stores

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.elnix.notes.data.settings.dataStore

object PluginsSettingsStore {

    // AlphaLM app access permission
    private val ALLOW_ALPHALLM_ACCESS = booleanPreferencesKey("allow_alphallm_access")
    
    fun getAllowAlphaLMAccess(ctx: Context): Flow<Boolean> =
        ctx.dataStore.data.map { it[ALLOW_ALPHALLM_ACCESS] ?: false }

    suspend fun setAllowAlphaLMAccess(ctx: Context, allowed: Boolean) {
        ctx.dataStore.edit { it[ALLOW_ALPHALLM_ACCESS] = allowed }
        // Also write to SharedPreferences for synchronous access in ContentProvider
        ctx.getSharedPreferences("notes_prefs", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("allow_alphallm_access", allowed)
            .apply()
    }

    suspend fun resetAll(ctx: Context) {
        ctx.dataStore.edit { prefs ->
            prefs.remove(ALLOW_ALPHALLM_ACCESS)
        }
    }

    suspend fun getAll(ctx: Context): Map<String, Boolean> {
        val prefs = ctx.dataStore.data.first()
        return buildMap {
            prefs[ALLOW_ALPHALLM_ACCESS]?.let { put(ALLOW_ALPHALLM_ACCESS.name, it) }

        }
    }

    suspend fun setAll(ctx: Context, data: Map<String, Boolean>) {
        ctx.dataStore.edit { prefs ->
            data[ALLOW_ALPHALLM_ACCESS.name]?.let { prefs[ALLOW_ALPHALLM_ACCESS] = it }
        }
    }
}
