package org.elnix.notes.ui.helpers.reminders

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.elnix.notes.R
import org.elnix.notes.data.ReminderEntity
import org.elnix.notes.data.helpers.OffsetItem
import kotlin.math.abs

@Composable
fun TimeBubble(
    reminder: ReminderEntity? = null,
    offsetObject: OffsetItem? = null,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    enabled: Boolean = true
) {
    require(reminder != null || offsetObject != null) {
        "Either reminder or offsetObject must be provided"
    }

    val currentTime = remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1000)
            currentTime.longValue = System.currentTimeMillis()
        }
    }

    val (triple, color) = when {
        reminder != null -> {
            val diffMillis = reminder.dueDateTime.timeInMillis - currentTime.longValue
            val isPast = diffMillis < 0
            val absDiffSec = (abs(diffMillis) / 1000).toInt()
            val (text, ratio) = getTextAndRatioFromOffset(absDiffSec)
            val display = if (isPast) "$text ago" else text
            val color = if (isPast) Color(0xFF2196F3)
            else Color.hsv(120f * ratio, 0.9f, 0.9f)
            Triple(display, ratio, isPast) to color
        }
        offsetObject != null -> {
            val absDiffSec = abs(offsetObject.offset)
            val (text, ratio) = getTextAndRatioFromOffset(absDiffSec)
            val color = Color.hsv(120f * ratio, 0.9f, 0.9f)
            Triple(text, ratio, false) to color
        }
        else -> error("Invalid input")
    }

    val displayText = triple.first

    Row(
        modifier = Modifier
            .padding(4.dp)
            .then(
                if (onClick != null || onLongClick != null) {
                    Modifier.combinedClickable(
                        onClick = { onClick?.invoke() },
                        onLongClick = onLongClick
                    )
                } else Modifier
            )
            .border(
                width = 1.dp,
                color = color.copy(alpha = if (enabled) 1f else 0.3f),
                shape = CircleShape
            )
            .background(
                color = color.copy(alpha = if (enabled) 0.15f else 0.05f),
                shape = CircleShape
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = displayText,
            color = color.copy(alpha = if (enabled) 1f else 0.4f)
        )
        if (onDelete != null) {
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(18.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = stringResource(R.string.delete),
                    tint = color.copy(alpha = if (enabled) 1f else 0.4f)
                )
            }
        }
    }
}


fun getTextAndRatioFromOffset(
    offsetSeconds: Int
): Pair<String, Float> {
    return when {
        offsetSeconds < 60 -> {
            // Less than 1 minute: show seconds only
            "$offsetSeconds s" to 0f
        }
        offsetSeconds < 3600 -> {
            // Minutes and seconds
            val minutes = offsetSeconds / 60
            val seconds = offsetSeconds % 60
            if (seconds == 0) {
                "$minutes min" to 0.1f
            } else {
                "$minutes min ${seconds}s" to 0.1f
            }
        }
        offsetSeconds < 86400 -> {
            // Hours and remainder minutes
            val hours = offsetSeconds / 3600
            val minutes = (offsetSeconds % 3600) / 60
            if (minutes == 0) {
                "$hours h" to 0.3f
            } else {
                "$hours h ${minutes}min" to 0.3f
            }
        }
        offsetSeconds < 7 * 86400 -> {
            // Days and remainder hours
            val days = offsetSeconds / 86400
            val hours = (offsetSeconds % 86400) / 3600
            if (hours == 0) {
                "$days d" to 0.6f
            } else {
                "$days d ${hours}h" to 0.6f
            }
        }
        else -> {
            // Weeks and remainder days
            val weeks = offsetSeconds / (7 * 86400)
            val days = (offsetSeconds % (7 * 86400)) / 86400
            if (days == 0) {
                "$weeks wk" to 1f
            } else {
                "$weeks wk ${days}d" to 1f
            }
        }
    }
}




fun getTextOffset(offset: Int): String {
    return getTextAndRatioFromOffset(offset).first
}

