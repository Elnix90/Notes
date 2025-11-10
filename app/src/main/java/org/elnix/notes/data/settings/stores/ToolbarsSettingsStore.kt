package org.elnix.notes.data.settings.stores

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.elnix.notes.data.helpers.ToolBars
import org.elnix.notes.data.settings.dataStore

data class ToolbarSetting(
    val toolbar: ToolBars,
    val enabled: Boolean = true,
    val color: Color? = null,
    val borderColor: Color? = null,
    val borderRadius: Int = 50,
    val borderWidth: Int = 2,
    val elevation: Int = 3,
    val leftPadding: Int = 16,
    val rightPadding: Int = 16
)

object ToolbarsSettingsStore {
    private val TOOLBARS_KEY = stringPreferencesKey("toolbars_settings")
    private val gson = Gson()

    val defaultList = listOf(
        ToolbarSetting(ToolBars.SELECT, true),
        ToolbarSetting(ToolBars.SEPARATOR, true),
        ToolbarSetting(ToolBars.TAGS, true),
        ToolbarSetting(ToolBars.QUICK_ACTIONS, true),
    )

    private val listType = object : TypeToken<List<ToolbarSetting>>() {}.type

    fun getToolbarsFlow(ctx: Context): Flow<List<ToolbarSetting>> =
        ctx.dataStore.data.map { prefs ->
            val raw = prefs[TOOLBARS_KEY]
            if (raw.isNullOrBlank()) {
                defaultList
            } else {
                runCatching { gson.fromJson<List<ToolbarSetting>>(raw, listType) }
                    .getOrDefault(defaultList)
            }
        }

    suspend fun setToolbars(ctx: Context, list: List<ToolbarSetting>) {
        ctx.dataStore.edit { prefs ->
            prefs[TOOLBARS_KEY] = gson.toJson(list)
        }
    }

    suspend fun updateToolbarColor(
        ctx: Context,
        toolbar: ToolBars,
        color: Color?,
        borderColor: Color?
    ) {
        ctx.dataStore.edit { prefs ->
            val raw = prefs[TOOLBARS_KEY]
            val currentList = if (raw.isNullOrBlank()) {
                defaultList
            } else {
                runCatching { gson.fromJson<List<ToolbarSetting>>(raw, listType) }
                    .getOrDefault(defaultList)
            }

            val updatedList = currentList.map {
                if (it.toolbar == toolbar) it.copy(color = color, borderColor = borderColor)
                else it
            }

            prefs[TOOLBARS_KEY] = gson.toJson(updatedList)
        }
    }

    suspend fun updateToolbarSetting(
        ctx: Context,
        toolbar: ToolBars,
        modifier: (ToolbarSetting) -> ToolbarSetting
    ) {
        withContext(Dispatchers.IO) {
            ctx.dataStore.edit { prefs ->
                val raw = prefs[TOOLBARS_KEY]
                val currentList = if (raw.isNullOrBlank()) {
                    defaultList
                } else {
                    runCatching { gson.fromJson<List<ToolbarSetting>>(raw, listType) }
                        .getOrDefault(defaultList)
                }

                val updatedList = currentList.map {
                    if (it.toolbar == toolbar) modifier(it)
                    else it
                }

                prefs[TOOLBARS_KEY] = gson.toJson(updatedList)
            }
        }
    }



//    private val TOOLBARS_BORDER = intPreferencesKey("toolbars_border")
//    fun getToolbarsBorder(ctx: Context): Flow<Int> =
//        ctx.dataStore.data.map { it[TOOLBARS_BORDER] ?: 2 }
//    suspend fun setToolbarsBorder(ctx: Context, size: Int) {
//        ctx.dataStore.edit { it[TOOLBARS_BORDER] = size }
//    }
//
//    private val TOOLBARS_CORNER = intPreferencesKey("toolbars_corner")
//    fun getToolbarsCorner(ctx: Context): Flow<Int> =
//        ctx.dataStore.data.map { it[TOOLBARS_CORNER] ?: 50 }
//    suspend fun setToolbarsCorner(ctx: Context, size: Int) {
//        ctx.dataStore.edit { it[TOOLBARS_CORNER] = size }
//    }
//
//    // Paddings
//    private val TOOLBARS_PADDING_LEFT = intPreferencesKey("toolbars_padding_left")
//    private val TOOLBARS_PADDING_RIGHT = intPreferencesKey("toolbars_padding_right")
    private val TOOLBARS_SPACING = intPreferencesKey("toolbars_spacing")
//
//
//    fun getToolbarsPaddingLeft(ctx: Context): Flow<Int> =
//        ctx.dataStore.data.map { it[TOOLBARS_PADDING_LEFT] ?: 16 }
//    fun getToolbarsPaddingRight(ctx: Context): Flow<Int> =
//        ctx.dataStore.data.map { it[TOOLBARS_PADDING_RIGHT] ?: 16 }
    fun getToolbarsSpacing(ctx: Context): Flow<Int> =
        ctx.dataStore.data.map { it[TOOLBARS_SPACING] ?: 8 }
//
//
//    suspend fun setToolbarsPaddingLeft(ctx: Context, size: Int) {
//        ctx.dataStore.edit { it[TOOLBARS_PADDING_LEFT] = size }
//    }
//    suspend fun setToolbarsPaddingRight(ctx: Context, size: Int) {
//        ctx.dataStore.edit { it[TOOLBARS_PADDING_RIGHT] = size }
//    }
    suspend fun setToolbarsSpacing(ctx: Context, size: Int) {
        ctx.dataStore.edit { it[TOOLBARS_SPACING] = size }
    }
}
