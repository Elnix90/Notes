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
import kotlinx.coroutines.flow.first
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


    suspend fun resetToolbar(ctx: Context, toolbar: ToolBars) {
        withContext(Dispatchers.IO) {
            ctx.dataStore.edit { prefs ->
                val raw = prefs[TOOLBARS_KEY]
                val currentList = if (raw.isNullOrBlank()) {
                    defaultList
                } else {
                    runCatching { gson.fromJson<List<ToolbarSetting>>(raw, listType) }
                        .getOrDefault(defaultList)
                }

                val defaultSetting = defaultList.find { it.toolbar == toolbar }
                    ?: return@edit // nothing to reset

                val updatedList = currentList.map {
                    if (it.toolbar == toolbar) defaultSetting else it
                }

                prefs[TOOLBARS_KEY] = gson.toJson(updatedList)
            }
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


    private val TOOLBARS_SPACING = intPreferencesKey("toolbars_spacing")
    fun getToolbarsSpacing(ctx: Context): Flow<Int> =
        ctx.dataStore.data.map { it[TOOLBARS_SPACING] ?: 8 }
    suspend fun setToolbarsSpacing(ctx: Context, size: Int) {
        ctx.dataStore.edit { it[TOOLBARS_SPACING] = size }
    }

    suspend fun resetAll(ctx: Context) {
        withContext(Dispatchers.IO) {
            ctx.dataStore.edit { prefs ->
                prefs.remove(TOOLBARS_KEY)
                prefs.remove(TOOLBARS_SPACING)
            }
        }
    }
    suspend fun getAll(ctx: Context): Map<String, String> {
        val prefs = ctx.dataStore.data.first()
        return buildMap {
            prefs[TOOLBARS_KEY]?.let { put(TOOLBARS_KEY.name, it) }
            prefs[TOOLBARS_SPACING]?.let { put(TOOLBARS_SPACING.name, it.toString()) }
        }
    }

    suspend fun setAll(ctx: Context, data: Map<String, String>) {
        ctx.dataStore.edit { prefs ->
            data[TOOLBARS_KEY.name]?.let { prefs[TOOLBARS_KEY] = it }
            data[TOOLBARS_SPACING.name]?.let { prefs[TOOLBARS_SPACING] = it.toIntOrNull() ?: 8 }
        }
    }

}
