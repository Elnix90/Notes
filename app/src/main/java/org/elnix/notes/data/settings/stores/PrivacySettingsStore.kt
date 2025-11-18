package org.elnix.notes.data.settings.stores

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.elnix.notes.data.settings.dataStore

object PrivacySettingsStore {
    private val BLOCK_SCREENSHOTS = booleanPreferencesKey("block_screenshots")
    fun getBlockScreenshots(ctx: Context): Flow<Boolean> =
        ctx.dataStore.data.map { it[BLOCK_SCREENSHOTS] ?: false }
    suspend fun setBlockScreenshots(ctx: Context, enabled: Boolean) {
        ctx.dataStore.edit { it[BLOCK_SCREENSHOTS] = enabled }
    }

    suspend fun resetAll(ctx: Context) {
        ctx.dataStore.edit { prefs ->
            prefs.remove(BLOCK_SCREENSHOTS)
        }
    }

    suspend fun getAll(ctx: Context): Map<String, Boolean> {
        val prefs = ctx.dataStore.data.first()
        return buildMap {
            prefs[BLOCK_SCREENSHOTS]?.let { put(BLOCK_SCREENSHOTS.name, it) }
        }
    }

    suspend fun setAll(ctx: Context, data: Map<String, Boolean>) {
        ctx.dataStore.edit { prefs ->
            data[BLOCK_SCREENSHOTS.name]?.let { prefs[BLOCK_SCREENSHOTS] = it }
        }
    }
}