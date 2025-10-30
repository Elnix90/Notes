package org.elnix.notes.data.helpers

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import org.elnix.notes.R

enum class ColorPickerMode {
    DEFAULTS,
    SLIDERS,
    GRADIENT
}

@Composable
fun colorPickerText(mode: ColorPickerMode): String {
    return when(mode){
        ColorPickerMode.SLIDERS -> stringResource(R.string.sliders)
        ColorPickerMode.GRADIENT -> stringResource(R.string.gradient)
        ColorPickerMode.DEFAULTS -> stringResource(R.string.default_text)
    }
}