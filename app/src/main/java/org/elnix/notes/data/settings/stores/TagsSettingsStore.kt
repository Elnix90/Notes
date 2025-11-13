package org.elnix.notes.data.settings.stores

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.elnix.notes.data.helpers.TagItem
import org.elnix.notes.data.settings.dataStore
import org.json.JSONArray
import org.json.JSONObject

object TagsSettingsStore {

    private val TAGS_KEY = stringPreferencesKey("app_tags")


    /** Get the list of saved tags */
    fun getTags(ctx: Context): Flow<List<TagItem>> =
        ctx.dataStore.data.map { prefs ->
            prefs[TAGS_KEY]?.let { jsonStr ->
                val arr = JSONArray(jsonStr)
                List(arr.length()) { i ->
                    val obj = arr.getJSONObject(i)
                    TagItem(
                        id = obj.optLong("id", 0L),
                        name = obj.optString("name", ""),
                        color = Color(obj.optInt("color", 0xFF000000.toInt())),
                        selected = obj.optBoolean("selected", true)
                    )
                }
            } ?: emptyList()
        }


    /** Save the entire list of tags */
    private suspend fun saveTags(ctx: Context, tags: List<TagItem>) {
        val jsonStr = JSONArray().apply {
            tags.forEach { tag ->
                put(JSONObject().apply {
                    put("id", tag.id)
                    put("name", tag.name)
                    put("color", tag.color.toArgb())
                    put("selected",tag.selected)
                })
            }
        }.toString()
        ctx.dataStore.edit { it[TAGS_KEY] = jsonStr }
    }

    /** Get tags once (suspend) */
    private suspend fun getTagsOnce(ctx: Context): MutableList<TagItem> =
        getTags(ctx).first().toMutableList()

    /** Add new tag */
    suspend fun addTag(ctx: Context, tag: TagItem) {
        val tags = getTagsOnce(ctx)
        tags.add(tag)
        saveTags(ctx, tags)
    }

    /** Update existing tag */
    suspend fun updateTag(ctx: Context, updated: TagItem) {
        val tags = getTagsOnce(ctx)
        val index = tags.indexOfFirst { it.id == updated.id }
        if (index != -1) {
            tags[index] = updated
            saveTags(ctx, tags)
        }
    }

    /** Delete tag by id */
    suspend fun deleteTag(ctx: Context, tag: TagItem) {
        val tags = getTagsOnce(ctx)
        tags.removeAll { it.id == tag.id }
        saveTags(ctx, tags)
    }


    /** Select or deselect all tags */
    suspend fun setAllTagsSelected(ctx: Context, selected: Boolean) {
        val tags = getTagsOnce(ctx)
        val updatedTags = tags.map { it.copy(selected = selected) }
        saveTags(ctx, updatedTags)
    }

    suspend fun resetAll(ctx: Context) {
        ctx.dataStore.edit { prefs ->
            prefs.remove(TAGS_KEY)
        }
    }

    suspend fun getAll(ctx: Context): Map<String, String> {
        val prefs = ctx.dataStore.data.first()
        return buildMap {
            prefs[TAGS_KEY]?.let { put(TAGS_KEY.name, it) }
        }
    }

    suspend fun setAll(ctx: Context, data: Map<String, String>) {
        ctx.dataStore.edit { prefs ->
            data[TAGS_KEY.name]?.let { prefs[TAGS_KEY] = it }
        }
    }

}
