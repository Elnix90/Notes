package org.elnix.notes.data.settings.stores

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.elnix.notes.data.helpers.GlobalNotesActions
import org.elnix.notes.data.helpers.ToolBars
import org.elnix.notes.data.helpers.defaultToolbarItems
import org.elnix.notes.data.settings.dataStore

data class ToolbarItemState(
    val action: GlobalNotesActions,
    val enabled: Boolean = false,
    val showLabel: Boolean = false,
    val bgColor: Color? = null,
    val onColor: Color? = null
)

object ToolbarItemsSettingsStore {

    private fun prefsKeyForToolbar(toolbar: ToolBars): Preferences.Key<String> =
        stringPreferencesKey("toolbar_items_${toolbar.name}")

    private val gson = Gson()
    private val listType = object : TypeToken<List<ToolbarItemState>>() {}.type

    fun getToolbarItemsFlow(ctx: Context, toolbar: ToolBars): Flow<List<ToolbarItemState>> {
        val key = prefsKeyForToolbar(toolbar)
        return ctx.dataStore.data.map { prefs ->
            val raw = prefs[key]
            if (raw.isNullOrEmpty()) {
                defaultToolbarItems(toolbar)
            } else {
                runCatching { gson.fromJson<List<ToolbarItemState>>(raw, listType) }
                    .getOrDefault(defaultToolbarItems(toolbar))
            }
        }
    }

    suspend fun setToolbarItems(ctx: Context, toolbar: ToolBars, newItems: List<ToolbarItemState>) {
        val key = prefsKeyForToolbar(toolbar)
        ctx.dataStore.edit { prefs ->
            prefs[key] = gson.toJson(newItems)
        }
    }

    suspend fun updateToolbarItemColor(
        ctx: Context,
        toolbar: ToolBars,
        action: GlobalNotesActions,
        newIconColor: Color?,
        newBgColor: Color?,
    ) {
        val key = prefsKeyForToolbar(toolbar)
        ctx.dataStore.edit { prefs ->
            val raw = prefs[key]
            val currentItems = if (raw.isNullOrEmpty()) {
                defaultToolbarItems(toolbar)
            } else {
                runCatching { gson.fromJson<List<ToolbarItemState>>(raw, listType) }
                    .getOrDefault(defaultToolbarItems(toolbar))
            }

            val updatedItems = currentItems.map { item ->
                if (item.action == action) item.copy(onColor = newIconColor, bgColor = newBgColor) else item
            }

            prefs[key] = gson.toJson(updatedItems)
        }
    }


    suspend fun resetToolbar(ctx: Context, toolbar: ToolBars) {
        val key = prefsKeyForToolbar(toolbar)
        ctx.dataStore.edit { prefs ->
            prefs[key] = Gson().toJson(defaultToolbarItems(toolbar))
        }
    }

    suspend fun getAll(ctx: Context): Map<String, String> {
        val prefs = ctx.dataStore.data.first()

        return buildMap {
            ToolBars.entries.forEach { toolbar ->
                val key = prefsKeyForToolbar(toolbar)
                prefs[key]?.let { json ->
                    put(key.name, json)
                }
            }
        }
    }

    suspend fun setAll(ctx: Context, data: Map<String, String>) {
        ctx.dataStore.edit { prefs ->
            ToolBars.entries.forEach { toolbar ->
                val key = prefsKeyForToolbar(toolbar)
                data[key.name]?.let { json ->
                    prefs[key] = json
                }
            }
        }
    }


}
