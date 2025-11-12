package org.elnix.notes.data.settings.stores

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.elnix.notes.data.helpers.NoteActionSettings
import org.elnix.notes.data.helpers.NotesActions
import org.elnix.notes.data.settings.dataStore

object ActionSettingsStore {
    private val SWIPE_LEFT_ACTION = stringPreferencesKey("swipe_left_action")
    private val SWIPE_RIGHT_ACTION = stringPreferencesKey("swipe_right_action")
    private val CLICK_ACTION = stringPreferencesKey("click_action")
    private val LONG_CLICK_ACTION = stringPreferencesKey("long_click_action")
    private val TYPE_BUTTON_ACTION = stringPreferencesKey("type_button_action")

    // --- Combined model
    fun getActionSettingsFlow(ctx: Context): Flow<NoteActionSettings> =
        ctx.dataStore.data.map { prefs ->
            NoteActionSettings(
                leftAction = prefs[SWIPE_LEFT_ACTION]?.let { NotesActions.valueOf(it) }
                    ?: NotesActions.DELETE,
                rightAction = prefs[SWIPE_RIGHT_ACTION]?.let { NotesActions.valueOf(it) }
                    ?: NotesActions.EDIT,
                clickAction = prefs[CLICK_ACTION]?.let { NotesActions.valueOf(it) }
                    ?: NotesActions.COMPLETE,
                longClickAction = prefs[LONG_CLICK_ACTION]?.let { NotesActions.valueOf(it) }
                    ?: NotesActions.SELECT,
                typeButtonAction = prefs[TYPE_BUTTON_ACTION]?.let { NotesActions.valueOf(it) }
                    ?: NotesActions.EDIT
            )
        }

    // --- Individual setters
    suspend fun setSwipeLeftAction(ctx: Context, action: NotesActions) {
        ctx.dataStore.edit { it[SWIPE_LEFT_ACTION] = action.name }
    }

    suspend fun setSwipeRightAction(ctx: Context, action: NotesActions) {
        ctx.dataStore.edit { it[SWIPE_RIGHT_ACTION] = action.name }
    }

    suspend fun setClickAction(ctx: Context, action: NotesActions) {
        ctx.dataStore.edit { it[CLICK_ACTION] = action.name }
    }

    suspend fun setLongClickAction(ctx: Context, action: NotesActions) {
        ctx.dataStore.edit { it[LONG_CLICK_ACTION] = action.name }
    }

    suspend fun setTypeButtonAction(ctx: Context, action: NotesActions) {
        ctx.dataStore.edit { it[TYPE_BUTTON_ACTION] = action.name }
    }

    suspend fun resetAll(ctx: Context) {
        ctx.dataStore.edit { prefs ->
            prefs.remove(SWIPE_LEFT_ACTION)
            prefs.remove(SWIPE_RIGHT_ACTION)
            prefs.remove(CLICK_ACTION)
            prefs.remove(LONG_CLICK_ACTION)
            prefs.remove(TYPE_BUTTON_ACTION)
        }
    }

    // --- Export all current settings as a map
    suspend fun exportAll(ctx: Context): Map<String, String> {
        val prefs = ctx.dataStore.data.first()
        return mapOf(
            "swipe_left_action" to (prefs[SWIPE_LEFT_ACTION] ?: NotesActions.DELETE.name),
            "swipe_right_action" to (prefs[SWIPE_RIGHT_ACTION] ?: NotesActions.EDIT.name),
            "click_action" to (prefs[CLICK_ACTION] ?: NotesActions.COMPLETE.name),
            "long_click_action" to (prefs[LONG_CLICK_ACTION] ?: NotesActions.SELECT.name),
            "type_button_action" to (prefs[TYPE_BUTTON_ACTION] ?: NotesActions.EDIT.name)
        )
    }

    // --- Apply all settings from a backup map
    suspend fun setAll(ctx: Context, backup: Map<String, String>) {
        ctx.dataStore.edit { prefs ->
            backup["swipe_left_action"]?.let { prefs[SWIPE_LEFT_ACTION] = it }
            backup["swipe_right_action"]?.let { prefs[SWIPE_RIGHT_ACTION] = it }
            backup["click_action"]?.let { prefs[CLICK_ACTION] = it }
            backup["long_click_action"]?.let { prefs[LONG_CLICK_ACTION] = it }
            backup["type_button_action"]?.let { prefs[TYPE_BUTTON_ACTION] = it }
        }
    }
}