package org.elnix.notes.data.settings.stores

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.elnix.notes.data.helpers.GlobalNotesActions
import org.elnix.notes.data.helpers.ToolBars
import org.elnix.notes.data.helpers.defaultToolbarItems

// Extension property to create DataStore instance in Context
private val Context.dataStore by preferencesDataStore(name = "toolbar_items_prefs")

data class ToolbarItemState(
    val action: GlobalNotesActions,
    val enabled: Boolean
)

object ToolbarItemsSettingsStore {

    private fun prefsKeyForToolbar(toolbar: ToolBars): Preferences.Key<Set<String>> =
        stringSetPreferencesKey("toolbar_items_${toolbar.name}")

    fun getToolbarItemsFlow(ctx: Context, toolbar: ToolBars): Flow<List<GlobalNotesActions>> {
        val key = prefsKeyForToolbar(toolbar)
        return ctx.dataStore.data.map { prefs ->
            val stored = prefs[key]?.mapNotNull { actionName ->
                GlobalNotesActions.entries.find { it.name == actionName }
            }
            stored?.takeIf { it.isNotEmpty() } ?: defaultToolbarItems(toolbar)
        }
    }


    suspend fun setToolbarItems(ctx: Context, toolbar: ToolBars, newItems: List<GlobalNotesActions>) {
        val key = prefsKeyForToolbar(toolbar)
        ctx.dataStore.edit { prefs ->
            prefs[key] = newItems.map { it.name }.toSet()
        }
    }
}
