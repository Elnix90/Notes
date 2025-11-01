package org.elnix.notes.data.settings.stores

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.elnix.notes.data.helpers.ColorPickerMode
import org.elnix.notes.data.helpers.NoteViewType
import org.elnix.notes.data.settings.ColorCustomisationMode
import org.elnix.notes.data.settings.DefaultThemes
import org.elnix.notes.data.settings.ShowNavBarActions
import org.elnix.notes.data.settings.dataStore

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


    private val SHOW_NOTES_NUMBER = booleanPreferencesKey("show_notes_number")
    fun getShowNotesNumber(ctx: Context): Flow<Boolean> = ctx.dataStore.data.map { it[SHOW_NOTES_NUMBER] ?: true }

    suspend fun setShowNotesNumber(ctx: Context, state: Boolean) {
        ctx.dataStore.edit { it[SHOW_NOTES_NUMBER] = state}
    }



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
                ?: DefaultThemes.DARK
        }

    suspend fun setDefaultTheme(ctx: Context, state: DefaultThemes) {
        ctx.dataStore.edit { it[DEFAULT_THEME] = state.name }
    }


    private val DEBUG_MODE_ENABLED = booleanPreferencesKey("debug_mode_enabled")
    fun getDebugMode(ctx: Context): Flow<Boolean> = ctx.dataStore.data.map { it[DEBUG_MODE_ENABLED] ?: false }

    suspend fun setDebugMode(ctx: Context, state: Boolean) {
        ctx.dataStore.edit { it[DEBUG_MODE_ENABLED] = state}
    }


    private val SHOW_DELETE_BUTTON = booleanPreferencesKey("show_delete_button")
    fun getShowDeleteButton(ctx: Context): Flow<Boolean> = ctx.dataStore.data.map { it[SHOW_DELETE_BUTTON] ?: false }
    suspend fun setShowDeleteButton(ctx: Context, state: Boolean) {
        ctx.dataStore.edit { it[SHOW_DELETE_BUTTON] = state}
    }


    private val NOTE_VIEW_TYPE = stringPreferencesKey("note_view_type")
    fun getNoteViewType(ctx: Context): Flow<NoteViewType> =
        ctx.dataStore.data.map { prefs ->
            prefs[NOTE_VIEW_TYPE]?.let { NoteViewType.valueOf(it) }
                ?: NoteViewType.LIST
        }
    suspend fun setNoteViewType(ctx: Context, state: NoteViewType) {
        ctx.dataStore.edit { it[NOTE_VIEW_TYPE] = state.name }
    }


    // Fullscreen options
    private val FULLSCREEN = booleanPreferencesKey("fullscreen")
    fun getFullscreen(ctx: Context): Flow<Boolean> =
        ctx.dataStore.data.map { it[FULLSCREEN] ?: false }
    suspend fun setFullscreen(ctx: Context, enabled: Boolean) {
        ctx.dataStore.edit { it[FULLSCREEN] = enabled }
    }

    private val SHOW_NOTE_TYPE_ICON = booleanPreferencesKey("show_note_type_icon")
    fun getShowNoteTypeIcon(ctx: Context): Flow<Boolean> =
        ctx.dataStore.data.map { it[SHOW_NOTE_TYPE_ICON] ?: false }
    suspend fun setShowNoteTypeIcon(ctx: Context, enabled: Boolean) {
        ctx.dataStore.edit { it[SHOW_NOTE_TYPE_ICON] = enabled }
    }
}