package org.elnix.notes.data.converters

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.TypeConverter

class ColorConverter {
    @TypeConverter
    fun fromColor(color: Color): Int = color.toArgb()

    @TypeConverter
    fun toColor(value: Int): Color = Color(value)
}
