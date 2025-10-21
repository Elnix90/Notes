package org.elnix.notes.ui.theme

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

fun Color.blendWith(other: Color, ratio: Float): Color {
    return Color(
        red = red * (1 - ratio) + other.red * ratio,
        green = green * (1 - ratio) + other.green * ratio,
        blue = blue * (1 - ratio) + other.blue * ratio,
        alpha = alpha
    )
}

fun Color.adjustBrightness(factor: Float): Color {
    return Color(
        red = (red * factor).coerceIn(0f, 1f),
        green = (green * factor).coerceIn(0f, 1f),
        blue = (blue * factor).coerceIn(0f, 1f),
        alpha = alpha
    )
}


object AppObjectsColors {

    @Composable
    fun defaultSwitchColors(): SwitchColors {
        return SwitchDefaults.colors(
            checkedThumbColor = MaterialTheme.colorScheme.primary,
            checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            checkedBorderColor = MaterialTheme.colorScheme.background,
            uncheckedThumbColor = MaterialTheme.colorScheme.background
                .blendWith(MaterialTheme.colorScheme.primary, 0.5f),
            uncheckedTrackColor = MaterialTheme.colorScheme.background,
            uncheckedBorderColor = MaterialTheme.colorScheme.background
                .blendWith(MaterialTheme.colorScheme.primary, 0.5f)
        )
    }

    @Composable
    fun defaultButtonColors(): ButtonColors {
        return ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onBackground
        )
    }

    @Composable
    fun cancelButtonColors(): ButtonColors {
        return ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary
        )
    }

    @Composable
    fun datePickerColors(): DatePickerColors {
        val colors = MaterialTheme.colorScheme

        return DatePickerDefaults.colors(
            containerColor = colors.surface,
            titleContentColor = colors.primary,
            headlineContentColor = colors.onPrimary,
            weekdayContentColor = colors.primary,
            subheadContentColor = colors.primary,
            navigationContentColor = colors.primary,
            yearContentColor = colors.onPrimary,
            disabledYearContentColor = colors.onSurface.copy(alpha = 0.38f),
            currentYearContentColor = colors.secondary,
            selectedYearContentColor = colors.primary,
            disabledSelectedYearContentColor = colors.onSurface.copy(alpha = 0.12f),
            selectedYearContainerColor = colors.primaryContainer,
            disabledSelectedYearContainerColor = colors.surfaceVariant,
            dayContentColor = colors.onSurface,
            disabledDayContentColor = colors.onSurface.copy(alpha = 0.38f),
            selectedDayContentColor = colors.onPrimary,
            disabledSelectedDayContentColor = colors.onSurface.copy(alpha = 0.12f),
            selectedDayContainerColor = colors.primary,
            disabledSelectedDayContainerColor = colors.surfaceVariant,
            todayContentColor = colors.secondary,
            todayDateBorderColor = colors.secondary,
            dayInSelectionRangeContentColor = colors.primary,
            dayInSelectionRangeContainerColor = colors.primary.copy(alpha = 0.24f),
            dividerColor = colors.outline,
            dateTextFieldColors = null
        )
    }


//    @Composable
//    fun timePickerColors(): TimePickerColors {
//        val colors = MaterialTheme.colorScheme
//        return TimePickerDefaults.colors(
//            containerColor = colors.background,           // Time picker container bg
//            hourDigitTextColor = colors.onSurface,        // Hour digits
//            minuteDigitTextColor = colors.onSurface,      // Minute digits
//            activeHourDigitTextColor = colors.onPrimary,  // Selected hour digit
//            activeMinuteDigitTextColor = colors.onPrimary,
//            activePeriodIconColor = colors.primary,       // AM/PM active icon color
//            inactivePeriodIconColor = colors.onSurfaceVariant,
//            selectionContainerColor = colors.primary,     // Circle around selected hour/minute
//        )
//    }


}


