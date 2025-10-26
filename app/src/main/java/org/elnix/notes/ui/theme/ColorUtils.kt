package org.elnix.notes.ui.theme

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
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
        val colors = MaterialTheme.colorScheme
        return SwitchDefaults.colors(
            checkedThumbColor = colors.onSurface,
            checkedTrackColor = colors.primary,
            checkedBorderColor = Color.Transparent,
            uncheckedThumbColor = colors.onSurface,
            uncheckedTrackColor = colors.background,
            uncheckedBorderColor = Color.Transparent
        )
    }

    @Composable
    fun buttonColors(): ButtonColors {
        val colors = MaterialTheme.colorScheme
        return ButtonDefaults.buttonColors(
            containerColor = colors.primary,
            contentColor =   colors.onBackground
        )
    }

    @Composable
    fun cancelButtonColors(): ButtonColors {
        val colors = MaterialTheme.colorScheme
        return ButtonDefaults.buttonColors(
            containerColor = colors.background,
            contentColor = colors.error
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
            checkedColor = colors.primary,
            uncheckedColor = colors.onBackground,
            checkmarkColor = colors.onSurface,
            disabledCheckedColor = colors.primary.copy(alpha = 0.5f),
            disabledUncheckedColor = colors.outline,
            disabledIndeterminateColor = colors.onSurface,
        )
    }

    @Composable
    fun outlinedTextFieldColors(): TextFieldColors {
        val colors = MaterialTheme.colorScheme
        return OutlinedTextFieldDefaults.colors(
            focusedTextColor = colors.onBackground,
            unfocusedTextColor = colors.onBackground,
            disabledTextColor = colors.onSurfaceVariant,
            errorTextColor = colors.error,

            focusedContainerColor = colors.background,
            unfocusedContainerColor = colors.background,
            disabledContainerColor = colors.background,
            errorContainerColor = colors.background,

            cursorColor = colors.primary,
            errorCursorColor = colors.error,

            focusedBorderColor = colors.primary,
            unfocusedBorderColor = colors.outline,
            disabledBorderColor = colors.surfaceVariant,
            errorBorderColor = colors.error,

            focusedLeadingIconColor = colors.primary,
            unfocusedLeadingIconColor = colors.onSurfaceVariant,
            disabledLeadingIconColor = colors.surfaceVariant,
            errorLeadingIconColor = colors.error,

            focusedTrailingIconColor = colors.primary,
            unfocusedTrailingIconColor = colors.onSurfaceVariant,
            disabledTrailingIconColor = colors.surfaceVariant,
            errorTrailingIconColor = colors.error,

            focusedLabelColor = colors.primary,
            unfocusedLabelColor = colors.onSurfaceVariant,
            disabledLabelColor = colors.surfaceVariant,
            errorLabelColor = colors.error,

            focusedPlaceholderColor = colors.onSurfaceVariant,
            unfocusedPlaceholderColor = colors.onSurfaceVariant,
            disabledPlaceholderColor = colors.surfaceVariant,
            errorPlaceholderColor = colors.error,

            focusedSupportingTextColor = colors.onSurfaceVariant,
            unfocusedSupportingTextColor = colors.onSurfaceVariant,
            disabledSupportingTextColor = colors.surfaceVariant,
            errorSupportingTextColor = colors.error,

            focusedPrefixColor = colors.onSurfaceVariant,
            unfocusedPrefixColor = colors.onSurfaceVariant,
            disabledPrefixColor = colors.surfaceVariant,
            errorPrefixColor = colors.error,

            focusedSuffixColor = colors.onSurfaceVariant,
            unfocusedSuffixColor = colors.onSurfaceVariant,
            disabledSuffixColor = colors.surfaceVariant,
            errorSuffixColor = colors.error
        )
    }


}


