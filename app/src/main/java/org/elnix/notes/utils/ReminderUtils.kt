package org.elnix.notes.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.elnix.notes.data.ReminderEntity
import java.util.Calendar
import kotlin.math.max

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

@Composable
fun ReminderBubble(
    reminder: ReminderEntity,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    val now = System.currentTimeMillis()
    val diffMillis = max(0, reminder.dueDateTime.timeInMillis - now)

    val (text, ratio) = when {
        diffMillis < 60_000 -> "<1 min" to 0f
        diffMillis < 3_600_000 -> "${diffMillis / 60_000} min" to 0.1f
        diffMillis < 86_400_000 -> "${diffMillis / 3_600_000} h" to 0.3f
        diffMillis < 7L * 86_400_000 -> "${diffMillis / 86_400_000} d" to 0.6f
        else -> "${diffMillis / (7L * 86_400_000)} wk" to 1f
    }

    // Redder when closer (ratio = 0 → red, ratio = 1 → green)
    val color = Color.hsv(
        hue = (120f * ratio), // 0 = red, 120 = green
        saturation = 0.9f,
        value = 0.9f
    )

    Row(
        modifier = Modifier
            .padding(4.dp)
            .border(
                width = 1.dp,
                color = color.copy(alpha = if (reminder.enabled) 1f else 0.3f),
                shape = CircleShape
            )
            .clickable { onToggle(!reminder.enabled) }
            .background(
                color = color.copy(alpha = if (reminder.enabled) 0.15f else 0.05f),
                shape = CircleShape
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            color = color.copy(alpha = if (reminder.enabled) 1f else 0.4f)
        )
        Spacer(Modifier.width(8.dp))
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(18.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Cancel,
                contentDescription = "Delete Reminder",
                tint = color.copy(if (reminder.enabled) 1f else 0.4f)
            )
        }
    }
}
