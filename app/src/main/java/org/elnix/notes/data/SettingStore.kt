// file: org/elnix/notes/data/SettingsStore.kt
package org.elnix.notes.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore by preferencesDataStore(name = "notes_prefs")

object SettingsStore {
    private val KEY_DARK = booleanPreferencesKey("dark_theme")

    fun isDarkFlow(context: Context) = context.dataStore.data
        .catch { e ->
            if (e is IOException) emit(emptyPreferences()) else throw e
        }
        .map { prefs -> prefs[KEY_DARK] ?: false }

    suspend fun setDark(context: Context, enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[KEY_DARK] = enabled }
    }
}
