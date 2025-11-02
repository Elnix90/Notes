package org.elnix.notes.utils

import java.util.Calendar

data class ReminderOffset(
    val minutesFromNow: Long? = null,
    val hourOfDay: Int? = null,
    val minute: Int? = null
) {
    fun toCalendar(): Calendar {
        val cal = Calendar.getInstance()

        minutesFromNow?.let {
            cal.add(Calendar.MINUTE, it.toInt())
        } ?: run {
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay ?: cal.get(Calendar.HOUR_OF_DAY))
            cal.set(Calendar.MINUTE, minute ?: cal.get(Calendar.MINUTE))
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            if (cal.timeInMillis <= System.currentTimeMillis()) {
                cal.add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        return cal
    }

    fun applyTo(calendar: Calendar) {
        when {
            minutesFromNow != null -> {
                calendar.add(Calendar.MINUTE, minutesFromNow.toInt())
            }
            hourOfDay != null && minute != null -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)
            }
        }
    }
}