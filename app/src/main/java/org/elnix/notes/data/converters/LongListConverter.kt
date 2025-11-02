package org.elnix.notes.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LongListConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromList(list: List<Long>?): String? {
        return list?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toList(json: String?): List<Long>? {
        if (json == null) return emptyList()
        val type = object : TypeToken<List<Long>>() {}.type
        return gson.fromJson(json, type)
    }
}
