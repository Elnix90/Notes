package org.elnix.notes.utils

import java.util.Calendar

/**
 * Unified representation of a reminder:
 *
 * three valid forms:
 * 1) IN  → secondsFromNow != null
 * 2) AT  → absoluteTimeMillis != null
 *
 * (Only one can be set at once)
 */
data class ReminderOffset(
    val secondsFromNow: Long? = null,
    val absoluteTimeMillis: Long? = null
) {

    init {
        require(!(secondsFromNow != null && absoluteTimeMillis != null)) {
            "ReminderOffset cannot contain both absolute date and seconds offset."
        }
        require(secondsFromNow == null || secondsFromNow >= 0) {
            "secondsFromNow cannot be negative."
        }
    }

    /** Convert to a Calendar instance representing the exact time. */
    fun toCalendar(): Calendar {
        val cal = Calendar.getInstance()

        when {
            secondsFromNow != null -> {
                cal.timeInMillis = System.currentTimeMillis() + secondsFromNow * 1000
            }
            absoluteTimeMillis != null -> {
                cal.timeInMillis = absoluteTimeMillis
            }
            else -> error("Invalid ReminderOffset: no representation is set.")
        }

        return cal
    }

//    fun toOffsetItem(): OffsetItem {
//        val cal = this.toCalendar()
//        val now = Calendar.getInstance()
//        val diffMillis = cal.timeInMillis - now.timeInMillis
//        val offsetSeconds = (diffMillis / 1000).toInt()
//
//        return OffsetItem(
//            id = System.currentTimeMillis(),
//            offset = offsetSeconds
//        )
//    }
//
//
    val isAbsolute = absoluteTimeMillis != null
//    val isOffset = secondsFromNow != null

//    /** Apply this reminder to an existing calendar instance. */
//    fun applyTo(calendar: Calendar) {
//        when {
//            secondsFromNow != null -> {
//                calendar.timeInMillis = System.currentTimeMillis() + secondsFromNow * 1000
//            }
//            absoluteTimeMillis != null -> {
//                calendar.timeInMillis = absoluteTimeMillis
//            }
//        }
//    }
}
