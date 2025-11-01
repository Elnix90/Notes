package org.elnix.notes.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.elnix.notes.data.ChecklistItem

class CheckListConverters {
    private val gson = Gson()
    @TypeConverter
    fun fromChecklist(items: List<ChecklistItem>?): String? {
        return items?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toChecklist(json: String?): List<ChecklistItem>? {
        if (json == null) return null
        val type = object : TypeToken<List<ChecklistItem>>() {}.type
        return gson.fromJson(json, type)
    }
}