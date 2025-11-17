package org.elnix.notes.data.settings.stores

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.elnix.notes.data.helpers.NoteViewType
import org.elnix.notes.data.settings.dataStore

object UiSettingsStore {

    data class UiSettingsBackup(
        val showNotesNumber: Boolean = true,
        val noteViewType: NoteViewType = NoteViewType.LIST,
        val fullscreen: Boolean = false,
        val showColorDropdownEditors: Boolean = false,
        val showReminderDropdownEditors: Boolean = false,
        val showQuickActionsDropdownEditors: Boolean = false,
        val showTagsDropdownEditors: Boolean = false,
        val showTagsInNotes: Boolean = true,
        val showBottomDeleteButton: Boolean = false,
        val hasShownWelcome: Boolean = false,
        val lastSeenVersion: Int = 0
    )


    private val SHOW_NOTES_NUMBER = booleanPreferencesKey("show_notes_number")
    fun getShowNotesNumber(ctx: Context): Flow<Boolean> = ctx.dataStore.data.map { it[SHOW_NOTES_NUMBER] ?: true }

    suspend fun setShowNotesNumber(ctx: Context, state: Boolean) {
        ctx.dataStore.edit { it[SHOW_NOTES_NUMBER] = state}
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

    private val SHOW_BOTTOM_DELETE_BUTTON = booleanPreferencesKey("show_bottom_delete_button")
    fun getShowBottomDeleteButton(ctx: Context): Flow<Boolean> =
        ctx.dataStore.data.map { it[SHOW_BOTTOM_DELETE_BUTTON] ?: false }
    suspend fun setShowBottomDeleteButton(ctx: Context, enabled: Boolean) {
        ctx.dataStore.edit { it[SHOW_BOTTOM_DELETE_BUTTON] = enabled }
    }

    private val HAS_SHOWN_WELCOME = booleanPreferencesKey("has_shown_welcome")
    fun getHasShownWelcome(ctx: Context): Flow<Boolean> =
        ctx.dataStore.data.map { it[HAS_SHOWN_WELCOME] ?: false }
    suspend fun setHasShownWelcome(ctx: Context, enabled: Boolean) {
        ctx.dataStore.edit { it[HAS_SHOWN_WELCOME] = enabled }
    }

    private val LAST_SEEN_VERSION = intPreferencesKey("last_seen_version")
    fun getLastSeenVersion(ctx: Context): Flow<Int> =
        ctx.dataStore.data.map { it[LAST_SEEN_VERSION] ?: 0 }
    suspend fun setLastSeenVersion(ctx: Context, version: Int) {
        ctx.dataStore.edit { it[LAST_SEEN_VERSION] = version }
    }

    suspend fun resetAll(ctx: Context) {
        ctx.dataStore.edit { prefs ->
            prefs.remove(SHOW_NOTES_NUMBER)
            prefs.remove(NOTE_VIEW_TYPE)
            prefs.remove(FULLSCREEN)
            prefs.remove(SHOW_COLOR_DROPDOWN_EDITORS)
            prefs.remove(SHOW_REMINDER_DROPDOWN_EDITORS)
            prefs.remove(SHOW_QUICK_ACTIONS_DROPDOWN_EDITORS)
            prefs.remove(SHOW_TAGS_DROPDOWN_EDITORS)
            prefs.remove(SHOW_TAGS_IN_NOTES)
            prefs.remove(SHOW_BOTTOM_DELETE_BUTTON)
            prefs.remove(HAS_SHOWN_WELCOME)
            prefs.remove(LAST_SEEN_VERSION)
        }
    }

//    suspend fun getAll(ctx: Context): UiSettingsBackup {
//        val prefs = ctx.dataStore.data.first()
//        return UiSettingsBackup(
//            showNotesNumber = prefs[SHOW_NOTES_NUMBER] ?: true,
//            noteViewType = prefs[NOTE_VIEW_TYPE]?.let { NoteViewType.valueOf(it) } ?: NoteViewType.LIST,
//            fullscreen = prefs[FULLSCREEN] ?: false,
//            showColorDropdownEditors = prefs[SHOW_COLOR_DROPDOWN_EDITORS] ?: false,
//            showReminderDropdownEditors = prefs[SHOW_REMINDER_DROPDOWN_EDITORS] ?: false,
//            showQuickActionsDropdownEditors = prefs[SHOW_QUICK_ACTIONS_DROPDOWN_EDITORS] ?: false,
//            showTagsDropdownEditors = prefs[SHOW_TAGS_DROPDOWN_EDITORS] ?: false,
//            showTagsInNotes = prefs[SHOW_TAGS_IN_NOTES] ?: true,
//            showBottomDeleteButton = prefs[SHOW_BOTTOM_DELETE_BUTTON] ?: false,
//            hasShownWelcome = prefs[HAS_SHOWN_WELCOME] ?: false,
//            lastSeenVersion = prefs[LAST_SEEN_VERSION] ?: 0
//        )
//    }
//
//    suspend fun setAll(ctx: Context, settings: UiSettingsBackup) {
//        ctx.dataStore.edit { prefs ->
//            prefs[SHOW_NOTES_NUMBER] = settings.showNotesNumber
//            prefs[NOTE_VIEW_TYPE] = settings.noteViewType.name
//            prefs[FULLSCREEN] = settings.fullscreen
//            prefs[SHOW_COLOR_DROPDOWN_EDITORS] = settings.showColorDropdownEditors
//            prefs[SHOW_REMINDER_DROPDOWN_EDITORS] = settings.showReminderDropdownEditors
//            prefs[SHOW_QUICK_ACTIONS_DROPDOWN_EDITORS] = settings.showQuickActionsDropdownEditors
//            prefs[SHOW_TAGS_DROPDOWN_EDITORS] = settings.showTagsDropdownEditors
//            prefs[SHOW_TAGS_IN_NOTES] = settings.showTagsInNotes
//            prefs[SHOW_BOTTOM_DELETE_BUTTON] = settings.showBottomDeleteButton
//            prefs[HAS_SHOWN_WELCOME] = settings.hasShownWelcome
//            prefs[LAST_SEEN_VERSION] = settings.lastSeenVersion
//        }
//    }



    suspend fun getAll(ctx: Context): Map<String, String> {
        val prefs = ctx.dataStore.data.first()
        val defaults = UiSettingsBackup()

        return buildMap {

            fun putIfNonDefault(key: String, value: Any?, default: Any?) {
                if (value != null && value != default) {
                    put(key, value.toString())
                }
            }

            putIfNonDefault(SHOW_NOTES_NUMBER.name,
                prefs[SHOW_NOTES_NUMBER],
                defaults.showNotesNumber
            )

            putIfNonDefault(NOTE_VIEW_TYPE.name,
                prefs[NOTE_VIEW_TYPE],
                defaults.noteViewType.name
            )

            putIfNonDefault(FULLSCREEN.name,
                prefs[FULLSCREEN],
                defaults.fullscreen
            )

            putIfNonDefault(SHOW_COLOR_DROPDOWN_EDITORS.name,
                prefs[SHOW_COLOR_DROPDOWN_EDITORS],
                defaults.showColorDropdownEditors
            )

            putIfNonDefault(SHOW_REMINDER_DROPDOWN_EDITORS.name,
                prefs[SHOW_REMINDER_DROPDOWN_EDITORS],
                defaults.showReminderDropdownEditors
            )

            putIfNonDefault(SHOW_QUICK_ACTIONS_DROPDOWN_EDITORS.name,
                prefs[SHOW_QUICK_ACTIONS_DROPDOWN_EDITORS],
                defaults.showQuickActionsDropdownEditors
            )

            putIfNonDefault(SHOW_TAGS_DROPDOWN_EDITORS.name,
                prefs[SHOW_TAGS_DROPDOWN_EDITORS],
                defaults.showTagsDropdownEditors
            )

            putIfNonDefault(SHOW_TAGS_IN_NOTES.name,
                prefs[SHOW_TAGS_IN_NOTES],
                defaults.showTagsInNotes
            )

            putIfNonDefault(SHOW_BOTTOM_DELETE_BUTTON.name,
                prefs[SHOW_BOTTOM_DELETE_BUTTON],
                defaults.showBottomDeleteButton
            )

            putIfNonDefault(HAS_SHOWN_WELCOME.name,
                prefs[HAS_SHOWN_WELCOME],
                defaults.hasShownWelcome
            )

            putIfNonDefault(LAST_SEEN_VERSION.name,
                prefs[LAST_SEEN_VERSION],
                defaults.lastSeenVersion
            )
        }
    }


    suspend fun setAll(ctx: Context, backup: Map<String, String>) {
        ctx.dataStore.edit { prefs ->

            backup[SHOW_NOTES_NUMBER.name]?.let {
                prefs[SHOW_NOTES_NUMBER] = it.toBoolean()
            }

            backup[NOTE_VIEW_TYPE.name]?.let {
                prefs[NOTE_VIEW_TYPE] = it
            }

            backup[FULLSCREEN.name]?.let {
                prefs[FULLSCREEN] = it.toBoolean()
            }

            backup[SHOW_COLOR_DROPDOWN_EDITORS.name]?.let {
                prefs[SHOW_COLOR_DROPDOWN_EDITORS] = it.toBoolean()
            }

            backup[SHOW_REMINDER_DROPDOWN_EDITORS.name]?.let {
                prefs[SHOW_REMINDER_DROPDOWN_EDITORS] = it.toBoolean()
            }

            backup[SHOW_QUICK_ACTIONS_DROPDOWN_EDITORS.name]?.let {
                prefs[SHOW_QUICK_ACTIONS_DROPDOWN_EDITORS] = it.toBoolean()
            }

            backup[SHOW_TAGS_DROPDOWN_EDITORS.name]?.let {
                prefs[SHOW_TAGS_DROPDOWN_EDITORS] = it.toBoolean()
            }

            backup[SHOW_TAGS_IN_NOTES.name]?.let {
                prefs[SHOW_TAGS_IN_NOTES] = it.toBoolean()
            }

            backup[SHOW_BOTTOM_DELETE_BUTTON.name]?.let {
                prefs[SHOW_BOTTOM_DELETE_BUTTON] = it.toBoolean()
            }

            backup[HAS_SHOWN_WELCOME.name]?.let {
                prefs[HAS_SHOWN_WELCOME] = it.toBoolean()
            }

            backup[LAST_SEEN_VERSION.name]?.let {
                prefs[LAST_SEEN_VERSION] = it.toInt()
            }
        }
    }

}