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

    val yearsFromToday: Int? = null,
    val monthsFromToday: Int? = null,     // Calendar.JANUARY = 0
    val daysFromToday: Int? = null,
    val hoursFromToday: Int? = null,      // 0..23
    val minutesFromToday: Int? = null,    // 0..59
    val secondsFromToday: Int? = null     // 0..59
) {

    init {
        // Rule 1: offset OR absolute, not both
        val hasAbsolute =
            yearsFromToday != null || monthsFromToday != null || daysFromToday != null ||
                    hoursFromToday != null || minutesFromToday != null || secondsFromToday != null

        require(!(secondsFromNow != null && hasAbsolute)) {
            "ReminderOffset cannot contain both offset and absolute date fields."
        }

        require(secondsFromNow == null || secondsFromNow >= 0) {
            "secondsFromNow cannot be negative."
        }
    }

    /** Build a Calendar from supplied fields or offset */
    fun toCalendar(acceptPast: Boolean = false): Calendar {
        val cal = Calendar.getInstance()

        when {
            // OFFSET MODE
            secondsFromNow != null -> {
                cal.timeInMillis = System.currentTimeMillis() + secondsFromNow * 1000
            }

            // ABSOLUTE DATE/TIME MODE (changed to relative from today midnight)
            else -> {
                // Initialize to today midnight
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)

                // Add offsets relative to today midnight
                if (yearsFromToday != null) cal.add(Calendar.YEAR, yearsFromToday)
                if (monthsFromToday != null) cal.add(Calendar.MONTH, monthsFromToday)
                if (daysFromToday != null) cal.add(Calendar.DAY_OF_MONTH, daysFromToday)
                if (hoursFromToday != null) cal.add(Calendar.HOUR_OF_DAY, hoursFromToday)
                if (minutesFromToday != null) cal.add(Calendar.MINUTE, minutesFromToday)
                if (secondsFromToday != null) cal.add(Calendar.SECOND, secondsFromToday)


                if (!acceptPast) {
                    val now = Calendar.getInstance()
                    if (cal.before(now)) {
                        cal.add(Calendar.DAY_OF_MONTH, 1)
                    }
                }
            }
        }

        return cal
    }

    val isOffset = secondsFromNow != null
    val isAbsolute = !isOffset
}


fun Calendar.toReminderOffset(): ReminderOffset {
    val now = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val yearsFromToday = get(Calendar.YEAR) - now.get(Calendar.YEAR)
    val monthsFromToday = get(Calendar.MONTH) - now.get(Calendar.MONTH)
    val daysFromToday = get(Calendar.DAY_OF_MONTH) - now.get(Calendar.DAY_OF_MONTH)
    val hoursFromToday = get(Calendar.HOUR_OF_DAY) - now.get(Calendar.HOUR_OF_DAY)
    val minutesFromToday = get(Calendar.MINUTE) - now.get(Calendar.MINUTE)
    val secondsFromToday = get(Calendar.SECOND) - now.get(Calendar.SECOND)

    return ReminderOffset(
        yearsFromToday = yearsFromToday,
        monthsFromToday = monthsFromToday,
        daysFromToday = daysFromToday,
        hoursFromToday = hoursFromToday,
        minutesFromToday = minutesFromToday,
        secondsFromToday = secondsFromToday
    )
}


fun Calendar.cloneCalendarDateOnly(): Calendar =
    (this.clone() as Calendar).apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
