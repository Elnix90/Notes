package org.elnix.notes.data.settings.stores

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.elnix.notes.data.settings.SwipeActionSettings
import org.elnix.notes.data.settings.SwipeActions
import org.elnix.notes.data.settings.dataStore

object ActionSettingsStore {
    private val SWIPE_LEFT_ACTION = stringPreferencesKey("swipe_left_action")
    private val SWIPE_RIGHT_ACTION = stringPreferencesKey("swipe_right_action")
    private val CLICK_ACTION = stringPreferencesKey("click_action")

    // --- Combined model
    fun getActionSettingsFlow(ctx: Context): Flow<SwipeActionSettings> =
        ctx.dataStore.data.map { prefs ->
            SwipeActionSettings(
                leftAction = prefs[SWIPE_LEFT_ACTION]?.let { SwipeActions.valueOf(it) }
                    ?: SwipeActions.DELETE,
                rightAction = prefs[SWIPE_RIGHT_ACTION]?.let { SwipeActions.valueOf(it) }
                    ?: SwipeActions.EDIT,
                clickAction = prefs[CLICK_ACTION]?.let { SwipeActions.valueOf(it) }
                    ?: SwipeActions.COMPLETE
            )
        }

    // --- Individual setters
    suspend fun setSwipeLeftAction(ctx: Context, action: SwipeActions) {
        ctx.dataStore.edit { it[SWIPE_LEFT_ACTION] = action.name }
    }

    suspend fun setSwipeRightAction(ctx: Context, action: SwipeActions) {
        ctx.dataStore.edit { it[SWIPE_RIGHT_ACTION] = action.name }
    }

    suspend fun setClickAction(ctx: Context, action: SwipeActions) {
        ctx.dataStore.edit { it[CLICK_ACTION] = action.name }
    }
}