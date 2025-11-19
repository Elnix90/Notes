package org.elnix.notes.utils

import java.util.Calendar

/**
 * Unified representation of a reminder:
 *
 * Two valid modes:
 * 1) IN  → secondsFromNow != null
 * 2) AT  → any of year/month/day/hour/minute is set
 */
data class ReminderOffset(
    val secondsFromNow: Long? = null,

    val year: Int? = null,
    val month: Int? = null,          // Calendar.JANUARY = 0
    val dayOfMonth: Int? = null,

    val dayOfWeek: Int? = null,      // Calendar.MONDAY...SUNDAY (optional)
    val hourOfDay: Int? = null,      // 0..23
    val minute: Int? = null          // 0..59
) {

    init {
        // Rule 1: offset OR absolute, not both
        val hasAbsolute =
            year != null || month != null || dayOfMonth != null ||
                    hourOfDay != null || minute != null || dayOfWeek != null

        require(!(secondsFromNow != null && hasAbsolute)) {
            "ReminderOffset cannot contain both offset and absolute date fields."
        }

        require(secondsFromNow == null || secondsFromNow >= 0) {
            "secondsFromNow cannot be negative."
        }
    }

    /** Build a Calendar from supplied fields or offset */
    fun toCalendar(): Calendar {
        val cal = Calendar.getInstance()

        when {
            // OFFSET MODE
            secondsFromNow != null -> {
                cal.timeInMillis = System.currentTimeMillis() + secondsFromNow * 1000
            }

            // ABSOLUTE DATE/TIME MODE
            else -> {
                // Start with now
                val y = year ?: cal.get(Calendar.YEAR)
                val m = month ?: cal.get(Calendar.MONTH)
                val d = dayOfMonth ?: cal.get(Calendar.DAY_OF_MONTH)
                val h = hourOfDay ?: 0
                val min = minute ?: 0

                cal.set(Calendar.YEAR, y)
                cal.set(Calendar.MONTH, m)
                cal.set(Calendar.DAY_OF_MONTH, d)
                cal.set(Calendar.HOUR_OF_DAY, h)
                cal.set(Calendar.MINUTE, min)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)

                // Optional: force day of week override
                if (dayOfWeek != null) {
                    cal.set(Calendar.DAY_OF_WEEK, dayOfWeek)
                }
            }
        }

        return cal
    }

    val isOffset = secondsFromNow != null
    val isAbsolute = !isOffset
}
