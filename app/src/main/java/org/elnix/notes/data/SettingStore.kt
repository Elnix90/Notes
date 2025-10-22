package org.elnix.notes.data

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow
import org.elnix.notes.ui.theme.Purple40
import org.elnix.notes.utils.ReminderOffset

private val Context.dataStore by preferencesDataStore(name = "settings")

object SettingsStore {

    private val SHOW_NAVBAR_LABELS = booleanPreferencesKey("navbar_labels")
    private val TEXT_COLOR = intPreferencesKey("text_color")
    private val PRIMARY_COLOR = intPreferencesKey("primary_color")
    private val BACKGROUND_COLOR = intPreferencesKey("background_color")

    fun getShowBottomNavLabelsFlow(ctx: Context): Flow<Boolean?> =
        ctx.dataStore.data.map { it[SHOW_NAVBAR_LABELS]}

    suspend fun setShowBottomNavLabelsFlow(ctx: Context, state: Boolean) {
        ctx.dataStore.edit { it[SHOW_NAVBAR_LABELS] = state }
    }

    fun getOnBackgroundFlow(ctx: Context): Flow<Int?> =
        ctx.dataStore.data.map { it[TEXT_COLOR] }

    suspend fun setOnBackground(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[TEXT_COLOR] = color }
    }

    fun getPrimaryFlow(ctx: Context): Flow<Int?> =
        ctx.dataStore.data.map { it[PRIMARY_COLOR] }

    suspend fun setPrimary(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[PRIMARY_COLOR] = color }
    }

    fun getBackgroundFlow(ctx: Context): Flow<Int?> =
        ctx.dataStore.data.map { it[BACKGROUND_COLOR] }

    suspend fun setBackground(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[BACKGROUND_COLOR] = color }
    }

    suspend fun resetColors(ctx: Context) {
        setPrimary(ctx, Purple40.toArgb())
        setBackground(ctx, Color.Black.toArgb())
        setOnBackground(ctx, Color.White.toArgb())
    }


    private val DEFAULT_REMINDERS = stringPreferencesKey("default_reminders")

    fun getDefaultRemindersFlow(ctx: Context): Flow<List<ReminderOffset>> =
        ctx.dataStore.data.map { prefs ->
            prefs[DEFAULT_REMINDERS]?.let { jsonStr ->
                val arr = org.json.JSONArray(jsonStr)
                List(arr.length()) { i ->
                    val obj = arr.getJSONObject(i)
                    ReminderOffset(
                        minutesFromNow = if (obj.has("minutes")) obj.getLong("minutes") else null,
                        hourOfDay = if (obj.has("hour")) obj.getInt("hour") else null,
                        minute = if (obj.has("minute")) obj.getInt("minute") else null
                    )
                }
            } ?: emptyList()
        }

    suspend fun setDefaultReminders(ctx: Context, reminders: List<ReminderOffset>) {
        val jsonStr = org.json.JSONArray().apply {
            reminders.forEach { r ->
                put(org.json.JSONObject().apply {
                    r.minutesFromNow?.let { put("minutes", it) }
                    r.hourOfDay?.let { put("hour", it) }
                    r.minute?.let { put("minute", it) }
                })
            }
        }.toString()

        ctx.dataStore.edit { it[DEFAULT_REMINDERS] = jsonStr }
    }


    private val SWIPE_LEFT_ACTION = stringPreferencesKey("swipe_left_action")
    private val SWIPE_RIGHT_ACTION = stringPreferencesKey("swipe_right_action")
    private val CLICK_ACTION = stringPreferencesKey("click_action")

    // --- Combined model
    fun getActionSettingsFlow(ctx: Context): Flow<ActionSettings> =
        ctx.dataStore.data.map { prefs ->
            ActionSettings(
                leftAction = prefs[SWIPE_LEFT_ACTION]?.let { Action.valueOf(it) } ?: Action.DELETE,
                rightAction = prefs[SWIPE_RIGHT_ACTION]?.let { Action.valueOf(it) } ?: Action.EDIT,
                clickAction = prefs[CLICK_ACTION]?.let { Action.valueOf(it) } ?: Action.COMPLETE
            )
        }

    // --- Individual setters
    suspend fun setSwipeLeftAction(ctx: Context, action: Action) {
        ctx.dataStore.edit { it[SWIPE_LEFT_ACTION] = action.name }
    }

    suspend fun setSwipeRightAction(ctx: Context, action: Action) {
        ctx.dataStore.edit { it[SWIPE_RIGHT_ACTION] = action.name }
    }

    suspend fun setClickAction(ctx: Context, action: Action) {
        ctx.dataStore.edit { it[CLICK_ACTION] = action.name }
    }


}
