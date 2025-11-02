package org.elnix.notes.ui.helpers.reminders

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
import kotlin.math.abs

@Composable
fun ReminderBubble(
    reminder: ReminderEntity,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    val now = System.currentTimeMillis()
    val diffMillis = reminder.dueDateTime.timeInMillis - now
    val isPast = diffMillis < 0
    val absDiff = abs(diffMillis)

    val (text, ratio) = when {
        absDiff < 60_000 -> "<1 min" to 0f
        absDiff < 3_600_000 -> "${absDiff / 60_000} min" to 0.1f
        absDiff < 86_400_000 -> "${absDiff / 3_600_000} h" to 0.3f
        absDiff < 7L * 86_400_000 -> "${absDiff / 86_400_000} d" to 0.6f
        else -> "${absDiff / (7L * 86_400_000)} wk" to 1f
    }

    val displayText = if (isPast) "$text ago" else text

    val color = if (isPast) {
        Color(0xFF2196F3)
    } else {
        // Gradient for upcoming events
        Color.hsv(
            hue = (120f * ratio),
            saturation = 0.9f,
            value = 0.9f
        )
    }


    Row(
        modifier = Modifier
            .padding(4.dp)
            .border(
                width = 1.dp,
                color = color.copy(alpha = if (reminder.enabled) 1f else 0.3f),
                shape = CircleShape
            )
            .clickable { if (!isPast) { onToggle(!reminder.enabled) } }
            .background(
                color = color.copy(alpha = if (reminder.enabled) 0.15f else 0.05f),
                shape = CircleShape
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = displayText,
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
                tint = color.copy(alpha = if (reminder.enabled) 1f else 0.4f)
            )
        }
    }
}
