package org.elnix.notes.data.settings.stores

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.elnix.notes.data.helpers.NoteViewType
import org.elnix.notes.data.settings.dataStore

object UiSettingsStore {


    private val SHOW_NOTES_NUMBER = booleanPreferencesKey("show_notes_number")
    fun getShowNotesNumber(ctx: Context): Flow<Boolean> = ctx.dataStore.data.map { it[SHOW_NOTES_NUMBER] ?: true }

    suspend fun setShowNotesNumber(ctx: Context, state: Boolean) {
        ctx.dataStore.edit { it[SHOW_NOTES_NUMBER] = state}
    }

    private val SHOW_DELETE_BUTTON = booleanPreferencesKey("show_delete_button")
    fun getShowDeleteButton(ctx: Context): Flow<Boolean> = ctx.dataStore.data.map { it[SHOW_DELETE_BUTTON] ?: true }
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
        ctx.dataStore.data.map { it[SHOW_NOTE_TYPE_ICON] ?: true }
    suspend fun setShowNoteTypeIcon(ctx: Context, enabled: Boolean) {
        ctx.dataStore.edit { it[SHOW_NOTE_TYPE_ICON] = enabled }
    }

    private val SHOW_COLOR_DROPDOWN_EDITORS = booleanPreferencesKey("show_color_dropdown_editors")
    fun getShowColorDropdownEditor(ctx: Context): Flow<Boolean> =
        ctx.dataStore.data.map { it[SHOW_COLOR_DROPDOWN_EDITORS] ?: false }
    suspend fun setShowColorDropdownEditor(ctx: Context, enabled: Boolean) {
        ctx.dataStore.edit { it[SHOW_COLOR_DROPDOWN_EDITORS] = enabled }
    }

    private val SHOW_REMINDER_DROPDOWN_EDITORS = booleanPreferencesKey("show_reminder_dropdown_editors")
    fun getShowReminderDropdownEditor(ctx: Context): Flow<Boolean> =
        ctx.dataStore.data.map { it[SHOW_REMINDER_DROPDOWN_EDITORS] ?: false }
    suspend fun setShowReminderDropdownEditor(ctx: Context, enabled: Boolean) {
        ctx.dataStore.edit { it[SHOW_REMINDER_DROPDOWN_EDITORS] = enabled }
    }

    private val SHOW_QUICK_ACTIONS_DROPDOWN_EDITORS = booleanPreferencesKey("show_quick_actions_dropdown_editors")
    fun getShowQuickActionsDropdownEditor(ctx: Context): Flow<Boolean> =
        ctx.dataStore.data.map { it[SHOW_QUICK_ACTIONS_DROPDOWN_EDITORS] ?: false }
    suspend fun setShowQuickActionsDropdownEditor(ctx: Context, enabled: Boolean) {
        ctx.dataStore.edit { it[SHOW_QUICK_ACTIONS_DROPDOWN_EDITORS] = enabled }
    }

    private val SHOW_TAGS_DROPDOWN_EDITORS = booleanPreferencesKey("show_tags_dropdown_editors")
    fun getShowTagsDropdownEditor(ctx: Context): Flow<Boolean> =
        ctx.dataStore.data.map { it[SHOW_TAGS_DROPDOWN_EDITORS] ?: false }
    suspend fun setShowTagsDropdownEditor(ctx: Context, enabled: Boolean) {
        ctx.dataStore.edit { it[SHOW_TAGS_DROPDOWN_EDITORS] = enabled }
    }

    private val SHOW_TAGS_IN_NOTES = booleanPreferencesKey("show_tags_in_notes")
    fun getShowTagsInNotes(ctx: Context): Flow<Boolean> =
        ctx.dataStore.data.map { it[SHOW_TAGS_IN_NOTES] ?: true }
    suspend fun setShowTagsInNotes(ctx: Context, enabled: Boolean) {
        ctx.dataStore.edit { it[SHOW_TAGS_IN_NOTES] = enabled }
    }

    suspend fun resetAll(ctx: Context) {
        ctx.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}