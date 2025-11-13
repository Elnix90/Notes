package org.elnix.notes.data.settings.stores

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.elnix.notes.data.helpers.ColorPickerMode
import org.elnix.notes.data.settings.ColorCustomisationMode
import org.elnix.notes.data.settings.DefaultThemes
import org.elnix.notes.data.settings.dataStore

object ColorModesSettingsStore {
    private val COLOR_PICKER_MODE = stringPreferencesKey("color_picker_mode")
    fun getColorPickerMode(ctx: Context): Flow<ColorPickerMode> =
        ctx.dataStore.data.map { prefs ->
            prefs[COLOR_PICKER_MODE]?.let { ColorPickerMode.valueOf(it) }
                ?: ColorPickerMode.SLIDERS
        }
    suspend fun setColorPickerMode(ctx: Context, state: ColorPickerMode) {
        ctx.dataStore.edit { it[COLOR_PICKER_MODE] = state.name}
    }


    private val COLOR_CUSTOMISATION_MODE = stringPreferencesKey("color_customisation_mode")
    fun getColorCustomisationMode(ctx: Context): Flow<ColorCustomisationMode> =
        ctx.dataStore.data.map { prefs ->
            prefs[COLOR_CUSTOMISATION_MODE]?.let { ColorCustomisationMode.valueOf(it) }
                ?: ColorCustomisationMode.DEFAULT
        }
    suspend fun setColorCustomisationMode(ctx: Context, state: ColorCustomisationMode) {
        ctx.dataStore.edit { it[COLOR_CUSTOMISATION_MODE] = state.name }
    }

    private val DEFAULT_THEME = stringPreferencesKey("default_theme")
    fun getDefaultTheme(ctx: Context): Flow<DefaultThemes> =
        ctx.dataStore.data.map { prefs ->
            prefs[DEFAULT_THEME]?.let { DefaultThemes.valueOf(it) }
                ?: DefaultThemes.AMOLED
        }
    suspend fun setDefaultTheme(ctx: Context, state: DefaultThemes) {
        ctx.dataStore.edit { it[DEFAULT_THEME] = state.name }
    }

    suspend fun resetAll(ctx: Context) {
        ctx.dataStore.edit { prefs ->
            prefs.remove(COLOR_PICKER_MODE)
            prefs.remove(COLOR_CUSTOMISATION_MODE)
            prefs.remove(DEFAULT_THEME)
        }
    }

    suspend fun getAll(ctx: Context): Map<String, String> {
        val prefs = ctx.dataStore.data.first()
        return buildMap {
            prefs[COLOR_PICKER_MODE]?.let { put(COLOR_PICKER_MODE.name, it) }
            prefs[COLOR_CUSTOMISATION_MODE]?.let { put(COLOR_CUSTOMISATION_MODE.name, it) }
            prefs[DEFAULT_THEME]?.let { put(DEFAULT_THEME.name, it) }
        }
    }

    suspend fun setAll(ctx: Context, data: Map<String, String>) {
        ctx.dataStore.edit { prefs ->
            data[COLOR_PICKER_MODE.name]?.let { prefs[COLOR_PICKER_MODE] = it }
            data[COLOR_CUSTOMISATION_MODE.name]?.let { prefs[COLOR_CUSTOMISATION_MODE] = it }
            data[DEFAULT_THEME.name]?.let { prefs[DEFAULT_THEME] = it }
        }
    }
}