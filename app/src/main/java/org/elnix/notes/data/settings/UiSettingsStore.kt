package org.elnix.notes.data.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object UiSettingsStore {
    private val SHOW_NAVBAR_LABELS = stringPreferencesKey("navbar_labels")

    fun getShowBottomNavLabelsFlow(ctx: Context): Flow<ShowNavBarActions> =
        ctx.dataStore.data.map { prefs ->
            prefs[SHOW_NAVBAR_LABELS]?.let { ShowNavBarActions.valueOf(it) }
                ?: ShowNavBarActions.ALWAYS
        }


    suspend fun setShowBottomNavLabelsFlow(ctx: Context, state: ShowNavBarActions) {
        ctx.dataStore.edit { it[SHOW_NAVBAR_LABELS] = state.name }
    }
}