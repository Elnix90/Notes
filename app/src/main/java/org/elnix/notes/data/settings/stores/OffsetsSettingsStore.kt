package org.elnix.notes.data.settings.stores

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.elnix.notes.data.helpers.OffsetItem
import org.elnix.notes.data.settings.dataStore
import org.json.JSONArray
import org.json.JSONObject

object OffsetsSettingsStore {
    private val OFFSETS_KEY = stringPreferencesKey("app_offsets")

    fun getOffsets(ctx: Context): Flow<List<OffsetItem>> =
        ctx.dataStore.data.map { prefs ->
            prefs[OFFSETS_KEY]?.let { jsonStr ->
                val arr = JSONArray(jsonStr)
                List(arr.length()) { i ->
                    val obj = arr.getJSONObject(i)
                    OffsetItem(
                        id = obj.optLong("id", 0L),
                        offset = obj.optInt("offset", 600),
                    )
                }
            } ?: emptyList()
        }

    private suspend fun saveOffsets(ctx: Context, offsets: List<OffsetItem>) {
        val jsonStr = JSONArray().apply {
            offsets.forEach { offset ->
                put(JSONObject().apply {
                    put("id", offset.id)
                    put("offset", offset.offset)
                })
            }
        }.toString()
        ctx.dataStore.edit { it[OFFSETS_KEY] = jsonStr }
    }

    /** Get offsets once (suspend) */
    private suspend fun getOffsetsOnce(ctx: Context): MutableList<OffsetItem> =
        getOffsets(ctx).first().toMutableList()

    /** Add new offset */
    suspend fun addOffset(ctx: Context, offset: OffsetItem) {
        val offsets = getOffsetsOnce(ctx)
        offsets.add(offset)
        saveOffsets(ctx, offsets)
    }

    /** Update existing offset */
    suspend fun updateOffset(ctx: Context, updated: OffsetItem) {
        val offsets = getOffsetsOnce(ctx)
        val index = offsets.indexOfFirst { it.id == updated.id }
        if (index != -1) {
            offsets[index] = updated
            saveOffsets(ctx, offsets)
        }
    }

    /** Delete offset by id */
    suspend fun deleteOffset(ctx: Context, offset: OffsetItem) {
        val offsets = getOffsetsOnce(ctx)
        offsets.removeAll { it.id == offset.id }
        saveOffsets(ctx, offsets)
    }

    /** Reset only offset preferences */
    suspend fun resetAll(ctx: Context) {
        ctx.dataStore.edit { prefs ->
            prefs.remove(OFFSETS_KEY)
        }
    }
}
