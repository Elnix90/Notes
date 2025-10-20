package org.elnix.notes.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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

data class ReminderOffset(
    val minutesFromNow: Long? = null,
    val hourOfDay: Int? = null,
    val minute: Int? = null
) {
    fun toCalendar(): Calendar {
        val cal = Calendar.getInstance()
        minutesFromNow?.let { cal.add(Calendar.MINUTE, it.toInt()) }
        hourOfDay?.let { cal.set(Calendar.HOUR_OF_DAY, it) }
        minute?.let { cal.set(Calendar.MINUTE, it) }
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal
    }
}

@Composable
fun ReminderBubble(
    reminder: ReminderEntity,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    val now = System.currentTimeMillis()
    val diffMillis = reminder.dueDateTime.timeInMillis - now

    val (text, urgency) = when {
        diffMillis <= 0 -> "Expired" to 1f
        diffMillis < 60_000 -> "<1 min" to 1f
        diffMillis < 3_600_000 -> "${diffMillis / 60_000} min" to 0.9f
        diffMillis < 86_400_000 -> "${diffMillis / 3_600_000} h" to 0.8f
        diffMillis < 30L * 86_400_000 -> "${diffMillis / 86_400_000} d" to 0.6f
        diffMillis < 365L * 86_400_000 -> "${diffMillis / (30L * 86_400_000)} mo" to 0.4f
        else -> "in ${(diffMillis / (365L * 86_400_000))} yr" to 0.2f
    }

    val color = Color.hsv(
        hue = 120f * urgency,
        saturation = 0.8f,
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
                tint = color.copy(alpha = 0.7f)
            )
        }
    }
}
