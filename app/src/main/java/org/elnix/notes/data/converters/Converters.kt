package org.elnix.notes.data.converters

import androidx.room.TypeConverter
import java.util.Calendar
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time

    @TypeConverter
    fun fromCalendar(calendar: Calendar?): Long? {
        return calendar?.timeInMillis
    }

    @TypeConverter
    fun toCalendar(millis: Long?): Calendar? {
        return millis?.let {
            Calendar.getInstance().apply { timeInMillis = it }
        }
    }
}