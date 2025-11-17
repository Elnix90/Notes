package org.elnix.notes.data.settings.stores

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.elnix.notes.data.helpers.SortMode
import org.elnix.notes.data.helpers.SortType
import org.elnix.notes.data.settings.dataStore

data class SortBackup(
    val sortMode: SortMode,
    val sortType: SortType
)

object SortSettingsStore {

    private val SORT_MODE = stringPreferencesKey("sort_mode")
    private val SORT_TYPE = stringPreferencesKey("sort_type")

    private val DEFAULT_SORT_MODE = SortMode.DESC
    private val DEFAULT_SORT_TYPE = SortType.DATE


    fun getSortMode(ctx: Context): Flow<SortMode> =
        ctx.dataStore.data.map { prefs ->
            prefs[SORT_MODE]
                ?.let { runCatching { enumValueOf<SortMode>(it) }.getOrNull() }
                ?: SortMode.DESC
        }

    fun getSortType(ctx: Context): Flow<SortType> =
        ctx.dataStore.data.map { prefs ->
            prefs[SORT_TYPE]
                ?.let { runCatching { enumValueOf<SortType>(it) }.getOrNull() }
                ?: SortType.DATE
        }


    suspend fun setSortMode(ctx: Context, mode: SortMode) {
        ctx.dataStore.edit { it[SORT_MODE] = mode.name }
    }

    suspend fun setSortType(ctx: Context, type: SortType) {
        ctx.dataStore.edit { it[SORT_TYPE] = type.name }
    }


    suspend fun resetAll(ctx: Context) {
        ctx.dataStore.edit { prefs ->
            prefs.remove(SORT_MODE)
            prefs.remove(SORT_TYPE)
        }
    }

    suspend fun getAll(ctx: Context): Map<String, String> {
        val prefs = ctx.dataStore.data.first()
        return buildMap {
            val currentMode = prefs[SORT_MODE]
            val currentType = prefs[SORT_TYPE]

            if (currentMode != null && currentMode != DEFAULT_SORT_MODE.name) {
                put(SORT_MODE.name, currentMode)
            }
            if (currentType != null && currentType != DEFAULT_SORT_TYPE.name) {
                put(SORT_TYPE.name, currentType)
            }
        }
    }

    suspend fun setAll(ctx: Context, backup: Map<String, String>) {
        ctx.dataStore.edit { prefs ->
            backup[SORT_MODE.name]?.let { prefs[SORT_MODE] = it }
            backup[SORT_TYPE.name]?.let { prefs[SORT_TYPE] = it }
        }
    }
}
