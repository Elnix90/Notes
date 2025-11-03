package org.elnix.notes.data.settings.stores

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.elnix.notes.data.helpers.ToolBars
import org.elnix.notes.data.settings.dataStore

data class ToolbarSetting(
    val toolbar: ToolBars,
    val enabled: Boolean = true
)

object ToolbarsSettingsStore {
    private val TOOLBARS_KEY = stringPreferencesKey("toolbars_settings")
    private val gson = Gson()

    private val defaultList = listOf(
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
}
