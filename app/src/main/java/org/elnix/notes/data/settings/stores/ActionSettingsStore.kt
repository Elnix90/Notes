package org.elnix.notes.data.settings.stores

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
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
}