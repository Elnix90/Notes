package org.elnix.notes.utils

import org.elnix.notes.data.helpers.OffsetItem
import java.util.Calendar

data class ReminderOffset(
    val secondsFromNow: Long,
) {
    fun toCalendar(): Calendar {
        val cal = Calendar.getInstance()

        secondsFromNow.let {
            cal.add(Calendar.SECOND, it.toInt())
        }
        return cal
    }

    fun toOffsetItem(): OffsetItem {
        val cal = this.toCalendar()
        val now = Calendar.getInstance()
        val diffMillis = cal.timeInMillis - now.timeInMillis
        val offsetSeconds = (diffMillis / 1000).toInt()

        return OffsetItem(
            id = System.currentTimeMillis(),
            offset = offsetSeconds
        )
    }

    fun applyTo(calendar: Calendar) {
        calendar.add(Calendar.SECOND, secondsFromNow.toInt())
    }
}
