package org.elnix.notes.data

import android.content.Context
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.elnix.notes.ui.theme.BackgroundDefault
import org.elnix.notes.ui.theme.CompleteDefault
import org.elnix.notes.ui.theme.DeleteDefault
import org.elnix.notes.ui.theme.EditDefault
import org.elnix.notes.ui.theme.ErrorDefault
import org.elnix.notes.ui.theme.OnBackgroundDefault
import org.elnix.notes.ui.theme.OnErrorDefault
import org.elnix.notes.ui.theme.OnPrimaryDefault
import org.elnix.notes.ui.theme.OnSecondaryDefault
import org.elnix.notes.ui.theme.OnSurfaceDefault
import org.elnix.notes.ui.theme.OnTertiaryDefault
import org.elnix.notes.ui.theme.OutlineDefault
import org.elnix.notes.ui.theme.PrimaryDefault
import org.elnix.notes.ui.theme.Secondary40
import org.elnix.notes.ui.theme.SurfaceDefault
import org.elnix.notes.ui.theme.Tertiary40
import org.elnix.notes.utils.ReminderOffset

private val Context.dataStore by preferencesDataStore(name = "settings")

object SettingsStore {

    private val SHOW_NAVBAR_LABELS = booleanPreferencesKey("navbar_labels")

    // --- COLORS ---

    private val PRIMARY_COLOR = intPreferencesKey("primary_color")
    private val ON_PRIMARY_COLOR = intPreferencesKey("on_primary_color")
    private val SECONDARY_COLOR = intPreferencesKey("secondary_color")
    private val ON_SECONDARY_COLOR = intPreferencesKey("on_secondary_color")
    private val TERTIARY_COLOR = intPreferencesKey("tertiary_color")
    private val ON_TERTIARY_COLOR = intPreferencesKey("on_tertiary_color")
    private val BACKGROUND_COLOR = intPreferencesKey("background_color")
    private val ON_BACKGROUND_COLOR = intPreferencesKey("on_background_color")

    private val SURFACE_COLOR = intPreferencesKey("surface_color")
    private val ON_SURFACE_COLOR = intPreferencesKey("on_surface_color")
    private val ERROR_COLOR = intPreferencesKey("error_color")
    private val ON_ERROR_COLOR = intPreferencesKey("on_error_color")

    private val OUTLINE_COLOR = intPreferencesKey("outline_color")

    private val DELETE_COLOR = intPreferencesKey("delete_color")
    private val EDIT_COLOR = intPreferencesKey("edit_color")
    private val COMPLETE_COLOR = intPreferencesKey("complete_color")


    fun getShowBottomNavLabelsFlow(ctx: Context): Flow<Boolean?> =
        ctx.dataStore.data.map { it[SHOW_NAVBAR_LABELS] }

    suspend fun setShowBottomNavLabelsFlow(ctx: Context, state: Boolean) {
        ctx.dataStore.edit { it[SHOW_NAVBAR_LABELS] = state }
    }


    fun getPrimaryFlow(ctx: Context) = ctx.dataStore.data.map { it[PRIMARY_COLOR] }
    suspend fun setPrimary(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[PRIMARY_COLOR] = color }
    }

    fun getOnPrimaryFlow(ctx: Context) = ctx.dataStore.data.map { it[ON_PRIMARY_COLOR] }
    suspend fun setOnPrimary(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[ON_PRIMARY_COLOR] = color }
    }

    fun getSecondaryFlow(ctx: Context) = ctx.dataStore.data.map { it[SECONDARY_COLOR] }
    suspend fun setSecondary(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[SECONDARY_COLOR] = color }
    }

    fun getOnSecondaryFlow(ctx: Context) = ctx.dataStore.data.map { it[ON_SECONDARY_COLOR] }
    suspend fun setOnSecondary(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[ON_SECONDARY_COLOR] = color }
    }

    fun getTertiaryFlow(ctx: Context) = ctx.dataStore.data.map { it[TERTIARY_COLOR] }
    suspend fun setTertiary(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[TERTIARY_COLOR] = color }
    }

    fun getOnTertiaryFlow(ctx: Context) = ctx.dataStore.data.map { it[ON_TERTIARY_COLOR] }
    suspend fun setOnTertiary(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[ON_TERTIARY_COLOR] = color }
    }


    fun getBackgroundFlow(ctx: Context) = ctx.dataStore.data.map { it[BACKGROUND_COLOR] }
    suspend fun setBackground(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[BACKGROUND_COLOR] = color }
    }

    fun getOnBackgroundFlow(ctx: Context) = ctx.dataStore.data.map { it[ON_BACKGROUND_COLOR] }
    suspend fun setOnBackground(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[ON_BACKGROUND_COLOR] = color }
    }


    fun getSurfaceFlow(ctx: Context) = ctx.dataStore.data.map { it[SURFACE_COLOR] }
    suspend fun setSurface(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[SURFACE_COLOR] = color }
    }

    fun getOnSurfaceFlow(ctx: Context) = ctx.dataStore.data.map { it[ON_SURFACE_COLOR] }
    suspend fun setOnSurface(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[ON_SURFACE_COLOR] = color }
    }

    fun getErrorFlow(ctx: Context) = ctx.dataStore.data.map { it[ERROR_COLOR] }
    suspend fun setError(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[ERROR_COLOR] = color }
    }

    fun getOnErrorFlow(ctx: Context) = ctx.dataStore.data.map { it[ON_ERROR_COLOR] }
    suspend fun setOnError(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[ON_ERROR_COLOR] = color }
    }

    fun getOutlineFlow(ctx: Context) = ctx.dataStore.data.map { it[OUTLINE_COLOR] }
    suspend fun setOutline(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[OUTLINE_COLOR] = color }
    }

    fun getDeleteFlow(ctx: Context) = ctx.dataStore.data.map { it[DELETE_COLOR] }
    suspend fun setDelete(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[DELETE_COLOR] = color }
    }

    fun getEditFlow(ctx: Context) = ctx.dataStore.data.map { it[EDIT_COLOR] }
    suspend fun setEdit(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[EDIT_COLOR] = color }
    }

    fun getCompleteFlow(ctx: Context) = ctx.dataStore.data.map { it[COMPLETE_COLOR] }
    suspend fun setComplete(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[COMPLETE_COLOR] = color }
    }


    suspend fun resetColors(ctx: Context) {
        ctx.dataStore.edit {
            it[PRIMARY_COLOR] = PrimaryDefault.toArgb()
            it[ON_PRIMARY_COLOR] = OnPrimaryDefault.toArgb()
            it[SECONDARY_COLOR] = Secondary40.toArgb()
            it[ON_SECONDARY_COLOR] = OnSecondaryDefault.toArgb()
            it[TERTIARY_COLOR] = Tertiary40.toArgb()
            it[ON_TERTIARY_COLOR] = OnTertiaryDefault.toArgb()
            it[BACKGROUND_COLOR] = BackgroundDefault.toArgb()
            it[ON_BACKGROUND_COLOR] = OnBackgroundDefault.toArgb()
            it[SURFACE_COLOR] = SurfaceDefault.toArgb()
            it[ON_SURFACE_COLOR] = OnSurfaceDefault.toArgb()
            it[ERROR_COLOR] = ErrorDefault.toArgb()
            it[ON_ERROR_COLOR] = OnErrorDefault.toArgb()
            it[OUTLINE_COLOR] = OutlineDefault.toArgb()
            it[DELETE_COLOR] = DeleteDefault.toArgb()
            it[EDIT_COLOR] = EditDefault.toArgb()
            it[COMPLETE_COLOR] = CompleteDefault.toArgb()
        }
    }


    // REMINDERS

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