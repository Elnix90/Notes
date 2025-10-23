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



    // --- COLORS ---

    private val PRIMARY_COLOR = intPreferencesKey("primary_color")
    private val ON_PRIMARY_COLOR = intPreferencesKey("on_primary_color")
    private val SECONDARY_COLOR = intPreferencesKey("secondary_color")
    private val ON_SECONDARY_COLOR = intPreferencesKey("on_secondary_color")
    private val TERTIARY_COLOR = intPreferencesKey("tertiary_color")
    private val ON_TERTIARY_COLOR = intPreferencesKey("on_tertiary_color")
    private val BACKGROUND_COLOR = intPreferencesKey("background_color")
    private val ON_BACKGROUND_COLOR = intPreferencesKey("on_background")

    private val SURFACE_COLOR = intPreferencesKey("surface_color")
    private val ON_SURFACE_COLOR = intPreferencesKey("on_surface_color")
    private val ERROR_COLOR = intPreferencesKey("error_color")
    private val ON_ERROR_COLOR = intPreferencesKey("on_error_color")
    private val PRIMARY_CONTAINER_COLOR = intPreferencesKey("primary_container_color")
    private val ON_PRIMARY_CONTAINER_COLOR = intPreferencesKey("on_primary_container_color")
    private val SECONDARY_CONTAINER_COLOR = intPreferencesKey("secondary_container_color")
    private val ON_SECONDARY_CONTAINER_COLOR = intPreferencesKey("on_secondary_container_color")

    private val SURFACE_VARIANT_COLOR = intPreferencesKey("surface_variant")
    private val ON_SURFACE_VARIANT_COLOR = intPreferencesKey("on_surface_variant_color")

    private val OUTLINE_COLOR = intPreferencesKey("outline_color")

    private val DELETE_COLOR = intPreferencesKey("delete_color")
    private val EDIT_COLOR = intPreferencesKey("edit_color")
    private val COMPLETE_COLOR = intPreferencesKey("complete_color")



    fun getShowBottomNavLabelsFlow(ctx: Context): Flow<Boolean?> =
        ctx.dataStore.data.map { it[SHOW_NAVBAR_LABELS]}
    suspend fun setShowBottomNavLabelsFlow(ctx: Context, state: Boolean) {
        ctx.dataStore.edit { it[SHOW_NAVBAR_LABELS] = state }
    }






    fun getPrimaryFlow(ctx: Context): Flow<Int?> =
        ctx.dataStore.data.map { it[PRIMARY_COLOR] }
    suspend fun setPrimary(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[PRIMARY_COLOR] = color }
    }
    fun getOnPrimaryFlow(ctx: Context): Flow<Int?> =
        ctx.dataStore.data.map { it[ON_PRIMARY_COLOR] }
    suspend fun setOnPrimary(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[ON_PRIMARY_COLOR] = color }
    }

    fun getSecondaryFlow(ctx: Context): Flow<Int?> =
        ctx.dataStore.data.map { it[SECONDARY_COLOR] }
    suspend fun setSecondary(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[SECONDARY_COLOR] = color }
    }
    fun getOnSecondaryFlow(ctx: Context): Flow<Int?> =
        ctx.dataStore.data.map { it[ON_SECONDARY_COLOR] }
    suspend fun setOnSecondary(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[ON_SECONDARY_COLOR] = color }
    }

    fun getTertiaryFlow(ctx: Context): Flow<Int?> =
        ctx.dataStore.data.map { it[TERTIARY_COLOR] }
    suspend fun setTertiary(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[TERTIARY_COLOR] = color }
    }
    fun getOnTertiaryFlow(ctx: Context): Flow<Int?> =
        ctx.dataStore.data.map { it[ON_TERTIARY_COLOR] }
    suspend fun setOnTertiary(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[ON_TERTIARY_COLOR] = color }
    }


    fun getBackgroundFlow(ctx: Context): Flow<Int?> =
        ctx.dataStore.data.map { it[BACKGROUND_COLOR] }
    suspend fun setBackground(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[BACKGROUND_COLOR] = color }
    }
    fun getOnBackgroundFlow(ctx: Context): Flow<Int?> =
        ctx.dataStore.data.map { it[ON_BACKGROUND_COLOR] }
    suspend fun setOnBackground(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[ON_BACKGROUND_COLOR] = color }
    }


    fun getSurfaceFlow(ctx: Context): Flow<Int?> =
        ctx.dataStore.data.map { it[SURFACE_COLOR] }
    suspend fun setSurface(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[SURFACE_COLOR] = color }
    }
    fun getOnSurfaceFlow(ctx: Context): Flow<Int?> =
        ctx.dataStore.data.map { it[ON_SURFACE_COLOR] }
    suspend fun setOnSurface(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[ON_SURFACE_COLOR] = color }
    }

    fun getErrorFlow(ctx: Context): Flow<Int?> =
        ctx.dataStore.data.map { it[ERROR_COLOR] }
    suspend fun setError(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[ERROR_COLOR] = color }
    }
    fun getOnErrorFlow(ctx: Context): Flow<Int?> =
        ctx.dataStore.data.map { it[ON_ERROR_COLOR] }
    suspend fun setOnError(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[ON_ERROR_COLOR] = color }
    }

    fun getPrimaryContainerFlow(ctx: Context): Flow<Int?> =
        ctx.dataStore.data.map { it[PRIMARY_CONTAINER_COLOR] }
    suspend fun setPrimaryContainer(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[PRIMARY_CONTAINER_COLOR] = color }
    }
    fun getOnPrimaryContainerFlow(ctx: Context): Flow<Int?> =
        ctx.dataStore.data.map { it[ON_PRIMARY_CONTAINER_COLOR] }
    suspend fun setOnPrimaryContainer(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[ON_PRIMARY_CONTAINER_COLOR] = color }
    }

    fun getSecondaryContainerFlow(ctx: Context): Flow<Int?> =
        ctx.dataStore.data.map { it[SECONDARY_CONTAINER_COLOR] }
    suspend fun setSecondaryContainer(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[SECONDARY_CONTAINER_COLOR] = color }
    }
    fun getOnSecondaryContainerFlow(ctx: Context): Flow<Int?> =
        ctx.dataStore.data.map { it[ON_SECONDARY_CONTAINER_COLOR] }
    suspend fun setOnSecondaryContainer(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[ON_SECONDARY_CONTAINER_COLOR] = color }
    }

    fun getSurfaceVariantFlow(ctx: Context): Flow<Int?> =
        ctx.dataStore.data.map { it[SURFACE_VARIANT_COLOR] }
    suspend fun setSurfaceVariant(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[SURFACE_VARIANT_COLOR] = color }
    }
    fun getOnSurfaceVariantFlow(ctx: Context): Flow<Int?> =
        ctx.dataStore.data.map { it[ON_SURFACE_VARIANT_COLOR] }
    suspend fun setOnSurfaceVariant(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[ON_SURFACE_VARIANT_COLOR] = color }
    }

    fun getOutlineFlow(ctx: Context): Flow<Int?> =
        ctx.dataStore.data.map { it[OUTLINE_COLOR] }
    suspend fun setOutline(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[OUTLINE_COLOR] = color }
    }

    fun getDeleteFlow(ctx: Context): Flow<Int?> =
        ctx.dataStore.data.map { it[DELETE_COLOR] }
    suspend fun setDelete(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[DELETE_COLOR] = color }
    }

    fun getEditFlow(ctx: Context): Flow<Int?> =
        ctx.dataStore.data.map { it[EDIT_COLOR] }
    suspend fun setEdit(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[EDIT_COLOR] = color }
    }

    fun getCompleteFlow(ctx: Context): Flow<Int?> =
        ctx.dataStore.data.map { it[COMPLETE_COLOR] }
    suspend fun setComplete(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[COMPLETE_COLOR] = color }
    }






    suspend fun resetColors(ctx: Context) {
        setPrimary(ctx, Purple40.toArgb())
        setBackground(ctx, Color.Black.toArgb())
        setOnBackground(ctx, Color.White.toArgb())
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
