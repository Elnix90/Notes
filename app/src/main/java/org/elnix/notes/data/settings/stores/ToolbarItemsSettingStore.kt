package org.elnix.notes.data.settings.stores

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.elnix.notes.data.helpers.GlobalNotesActions
import org.elnix.notes.data.helpers.ToolBars
import org.elnix.notes.data.helpers.defaultToolbarItems
import org.elnix.notes.data.settings.dataStore

data class ToolbarItemState(
    val action: GlobalNotesActions,
    val enabled: Boolean = false,
    val showLabel: Boolean = false,
    val color: Color? = null
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
        newColor: Color?
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
                if (item.action == action) item.copy(color = newColor) else item
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
}
