package org.elnix.notes.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.elnix.notes.data.helpers.TagItem


class TagConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromTags(tags: List<TagItem>?): String? {
        return tags?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toTags(json: String?): List<TagItem>? {
        if (json == null) return null
        val type = object : TypeToken<List<TagItem>>() {}.type
        return gson.fromJson(json, type)
    }
}
