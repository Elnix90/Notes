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

    suspend fun getAll(ctx: Context): SortBackup {
        val prefs = ctx.dataStore.data.first()

        val mode = prefs[SORT_MODE]
            ?.let { runCatching { enumValueOf<SortMode>(it) }.getOrNull() }
            ?: SortMode.DESC

        val type = prefs[SORT_TYPE]
            ?.let { runCatching { enumValueOf<SortType>(it) }.getOrNull() }
            ?: SortType.DATE

        return SortBackup(mode, type)
    }

    suspend fun setAll(ctx: Context, backup: SortBackup) {
        ctx.dataStore.edit { prefs ->
            prefs[SORT_MODE] = backup.sortMode.name
            prefs[SORT_TYPE] = backup.sortType.name
        }
    }
}
