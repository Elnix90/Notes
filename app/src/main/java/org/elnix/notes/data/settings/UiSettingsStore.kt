package org.elnix.notes.data.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object UiSettingsStore {
    private val SHOW_NAVBAR_LABELS = booleanPreferencesKey("navbar_labels")

    fun getShowBottomNavLabelsFlow(ctx: Context): Flow<Boolean?> =
        ctx.dataStore.data.map { it[SHOW_NAVBAR_LABELS] }

    suspend fun setShowBottomNavLabelsFlow(ctx: Context, state: Boolean) {
        ctx.dataStore.edit { it[SHOW_NAVBAR_LABELS] = state }
    }
}