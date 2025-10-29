package org.elnix.notes.data.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.elnix.notes.data.helpers.ColorPickerMode

object UiSettingsStore {
    private val SHOW_NAVBAR_LABELS = stringPreferencesKey("navbar_labels")
    private val SHOW_NOTES_NUMBER = booleanPreferencesKey("show_notes_number")

    private val COLOR_PICKER_MODE = stringPreferencesKey("color_picker_mode")

    fun getShowBottomNavLabelsFlow(ctx: Context): Flow<ShowNavBarActions> =
        ctx.dataStore.data.map { prefs ->
            prefs[SHOW_NAVBAR_LABELS]?.let { ShowNavBarActions.valueOf(it) }
                ?: ShowNavBarActions.ALWAYS
        }


    suspend fun setShowBottomNavLabelsFlow(ctx: Context, state: ShowNavBarActions) {
        ctx.dataStore.edit { it[SHOW_NAVBAR_LABELS] = state.name }
    }

    fun getShowNotesNumber(ctx: Context): Flow<Boolean> = ctx.dataStore.data.map { it[SHOW_NOTES_NUMBER] ?: true }

    suspend fun setShowNotesNumber(ctx: Context, state: Boolean) {
        ctx.dataStore.edit { it[SHOW_NOTES_NUMBER] = state}
    }


    fun getColorPickerMode(ctx: Context): Flow<ColorPickerMode> =
        ctx.dataStore.data.map { prefs ->
            prefs[COLOR_PICKER_MODE]?.let { ColorPickerMode.valueOf(it) }
                ?: ColorPickerMode.SLIDERS
        }

    suspend fun setColorPickerMode(ctx: Context, state: ColorPickerMode) {
        ctx.dataStore.edit { it[COLOR_PICKER_MODE] = state.name}
    }
}