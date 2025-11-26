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
import kotlinx.coroutines.delay
import org.elnix.notes.R
import org.elnix.notes.utils.ReminderOffset
import java.text.DateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs

@Composable
fun TimeBubble(
    reminderOffset: ReminderOffset,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    enabled: Boolean = true,
    showAbsoluteDate: Boolean? = null,
    expandToLargerUnits: Boolean = true
) {
    val currentTime = remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentTime.longValue = System.currentTimeMillis()
        }
    }

    val cal = reminderOffset.toCalendar()
    val diffMillis = cal.timeInMillis - currentTime.longValue
    val isPast = diffMillis < 0
    val absSeconds = abs(diffMillis / 1000).toInt()


    val (t, r) = getDisplayTextWithFutureHandling(
        absSeconds,
        expand = expandToLargerUnits
    )
    val (text, ratio) =
        if ((showAbsoluteDate == null && reminderOffset.isAbsolute) || showAbsoluteDate == true) {
            formatAbsolute(cal) to r
        } else {
            (if (isPast) "$t ago" else t) to r
        }

    val bubbleColor =
        if (isPast) Color(0xFF2196F3)
        else Color.hsv(120f * ratio, 0.9f, 0.9f)

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
                color = bubbleColor.copy(alpha = if (enabled) 1f else 0.3f),
                shape = CircleShape
            )
            .background(
                color = bubbleColor.copy(alpha = if (enabled) 0.15f else 0.05f),
                shape = CircleShape
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            color = bubbleColor.copy(alpha = if (enabled) 1f else 0.4f)
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
                    tint = bubbleColor.copy(alpha = if (enabled) 1f else 0.4f)
                )
            }
        }
    }
}


/* ---------------------------------------------------------
   DATE FORMATTERS + EXTENDED FAR-FUTURE HANDLING
--------------------------------------------------------- */

private fun formatAbsolute(cal: Calendar): String {
    val now = Calendar.getInstance()

    val hour = cal.get(Calendar.HOUR_OF_DAY)
    val minute = cal.get(Calendar.MINUTE)
    val timeStr = if (minute == 0) "at ${hour}h" else "at ${hour}h ${minute}m"

    val daysDiff = ((cal.timeInMillis - now.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()

    return when {
        // Today
        cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                cal.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR) -> {
            timeStr
        }
        // Tomorrow
        cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                cal.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR) + 1 -> {
            "Tomorrow $timeStr"
        }
        // Within the next 7 days
        daysDiff in 1..6 -> {
            val dayOfWeek = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
            "$dayOfWeek $timeStr"
        }
        // Beyond a week → full formatted date
        else -> {
            DateFormat.getDateTimeInstance(
                DateFormat.MEDIUM,
                DateFormat.SHORT,
                Locale.getDefault()
            ).format(cal.time)
        }
    }
}


private fun getDisplayTextWithFutureHandling(
    seconds: Int,
    expand: Boolean
): Pair<String, Float> {
    val oneMonthSec = 30 * 24 * 3600

    return if (expand && seconds >= oneMonthSec) {
        getLargeFutureUnit(seconds)
    } else {
        getTextAndRatioFromOffset(seconds)
    }
}

/**
 * Convert to weeks, months, or years.
 */
private fun getLargeFutureUnit(seconds: Int): Pair<String, Float> {
    val days = seconds / 86400
    val weeks = days / 7
    val months = days / 30
    val years = days / 365

    return when {
        days < 56 -> { // < 8 weeks
            "$weeks wk" to 0.8f
        }
        days < 730 -> { // < 24 months
            "$months mo" to 0.9f
        }
        else -> {
            "$years yr" to 1f
        }
    }
}


fun getTextAndRatioFromOffset(offsetSeconds: Int): Pair<String, Float> {
    return when {
        offsetSeconds < 60 -> {
            "$offsetSeconds s" to 0f
        }
        offsetSeconds < 3600 -> {
            val minutes = offsetSeconds / 60
            val seconds = offsetSeconds % 60
            if (seconds == 0) "$minutes min" to 0.1f
            else "$minutes min ${seconds}s" to 0.1f
        }
        offsetSeconds < 86400 -> {
            val hours = offsetSeconds / 3600
            val minutes = (offsetSeconds % 3600) / 60
            if (minutes == 0) "$hours h" to 0.3f
            else "$hours h ${minutes}min" to 0.3f
        }
        offsetSeconds < 7 * 86400 -> {
            val days = offsetSeconds / 86400
            val hours = (offsetSeconds % 86400) / 3600
            if (hours == 0) "$days d" to 0.6f
            else "$days d ${hours}h" to 0.6f
        }
        else -> {
            val future = System.currentTimeMillis() + offsetSeconds * 1000L
            val formattedDate = DateFormat.getDateTimeInstance(
                DateFormat.MEDIUM,
                DateFormat.SHORT,
                Locale.getDefault()
            ).format(Date(future))
            formattedDate to 1f
        }
    }
}

fun getTextOffset(offset: Int): String {
    return getTextAndRatioFromOffset(offset).first
}
