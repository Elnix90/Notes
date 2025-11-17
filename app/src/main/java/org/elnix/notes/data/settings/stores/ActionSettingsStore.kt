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
    private val LEFT_BUTTON_ACTION = stringPreferencesKey("left_button_action")
    private val RIGHT_BUTTON_ACTION = stringPreferencesKey("right_button_action")



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
                rightButtonAction = prefs[RIGHT_BUTTON_ACTION]?.let { NotesActions.valueOf(it) }
                    ?: NotesActions.DELETE,
                leftButtonAction = prefs[LEFT_BUTTON_ACTION]?.let { NotesActions.valueOf(it) }
                    ?: NotesActions.EDIT,
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

    suspend fun setLeftButtonAction(ctx: Context, action: NotesActions) {
        ctx.dataStore.edit { it[LEFT_BUTTON_ACTION] = action.name }
    }

    suspend fun setRightButtonAction(ctx: Context, action: NotesActions) {
        ctx.dataStore.edit { it[RIGHT_BUTTON_ACTION] = action.name }
    }

    suspend fun resetAll(ctx: Context) {
        ctx.dataStore.edit { prefs ->
            prefs.remove(SWIPE_LEFT_ACTION)
            prefs.remove(SWIPE_RIGHT_ACTION)
            prefs.remove(CLICK_ACTION)
            prefs.remove(LONG_CLICK_ACTION)
            prefs.remove(RIGHT_BUTTON_ACTION)
            prefs.remove(LEFT_BUTTON_ACTION)
        }
    }

    // --- Export all current settings as a map
    suspend fun getAll(ctx: Context): Map<String, String> {
        val prefs = ctx.dataStore.data.first()
        return buildMap {
            prefs[SWIPE_LEFT_ACTION]?.let { put(SWIPE_LEFT_ACTION.name, it) }
            prefs[SWIPE_RIGHT_ACTION]?.let { put(SWIPE_RIGHT_ACTION.name, it) }
            prefs[CLICK_ACTION]?.let { put(CLICK_ACTION.name, it) }
            prefs[LONG_CLICK_ACTION]?.let { put(LONG_CLICK_ACTION.name, it) }
            prefs[LEFT_BUTTON_ACTION]?.let { put(LEFT_BUTTON_ACTION.name, it) }
            prefs[RIGHT_BUTTON_ACTION]?.let { put(RIGHT_BUTTON_ACTION.name, it) }
        }
    }

    // --- Apply all settings from a backup map
    suspend fun setAll(ctx: Context, backup: Map<String, String>) {
        ctx.dataStore.edit { prefs ->
            backup[SWIPE_LEFT_ACTION.name]?.let { prefs[SWIPE_LEFT_ACTION] = it }
            backup[SWIPE_RIGHT_ACTION.name]?.let { prefs[SWIPE_RIGHT_ACTION] = it }
            backup[CLICK_ACTION.name]?.let { prefs[CLICK_ACTION] = it }
            backup[LONG_CLICK_ACTION.name]?.let { prefs[LONG_CLICK_ACTION] = it }
            backup[LEFT_BUTTON_ACTION.name]?.let { prefs[LEFT_BUTTON_ACTION] = it }
            backup[RIGHT_BUTTON_ACTION.name]?.let { prefs[RIGHT_BUTTON_ACTION] = it }
        }
    }
}