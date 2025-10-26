package org.elnix.notes.ui.theme

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
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
    fun switchColors(): SwitchColors {
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
    fun buttonColors(): ButtonColors {
        return ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onBackground
        )
    }

    @Composable
    fun cancelButtonColors(): ButtonColors {
        return ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.error
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
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


    @Composable
    fun sliderColors(): SliderColors {
        val colors = MaterialTheme.colorScheme
        return SliderDefaults.colors(
            thumbColor = colors.primary,
            activeTrackColor = colors.secondary,
            activeTickColor = colors.primary,
            inactiveTrackColor = colors.background,
            inactiveTickColor = colors.primary,
            disabledThumbColor = colors.primary,
            disabledActiveTrackColor = colors.onSurface,
            disabledActiveTickColor = colors.primary,
        )
    }


    @Composable
    fun dropDownMenuColors(): TextFieldColors {
        val colors = MaterialTheme.colorScheme
        return TextFieldDefaults.colors(
            focusedContainerColor = colors.secondary,
            unfocusedContainerColor = colors.secondary,
            disabledContainerColor = colors.surface,
            focusedIndicatorColor = colors.primary,
            unfocusedIndicatorColor = colors.outline,
            focusedTextColor = colors.onSurface,
            unfocusedTextColor = colors.onSurface
        )
    }

    @Composable
    fun checkboxColors(): CheckboxColors {
        val colors = MaterialTheme.colorScheme
        return CheckboxDefaults.colors(
            colors.primary,
            colors.onBackground,
            colors.onSurface,
            colors.primary.copy(alpha = 0.5f),
            colors.outline,
            colors.onSurface,
        )
    }

}


