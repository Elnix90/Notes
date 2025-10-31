package org.elnix.notes.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

fun generateColorScheme(
    primary: Color,
    onPrimary: Color,
    secondary: Color,
    onSecondary: Color,
    tertiary: Color,
    onTertiary: Color,
    background: Color,
    onBackground: Color,
    surface: Color,
    onSurface: Color,
    error: Color,
    onError: Color,
    outline: Color
): ColorScheme {

    val base = darkColorScheme()

    return base.copy(
        primary = primary,
        onPrimary = onPrimary,
        secondary = secondary,
        onSecondary = onSecondary,
        tertiary = tertiary,
        onTertiary = onTertiary,
        background = background,
        onBackground = onBackground,
        surface = surface,
        onSurface = onSurface,
        error = error,
        onError = onError,
        outline = outline
    )
}

@Composable
fun NotesTheme(
    customPrimary: Int? = null,
    customOnPrimary: Int? = null,
    customSecondary: Int? = null,
    customOnSecondary: Int? = null,
    customTertiary: Int? = null,
    customOnTertiary: Int? = null,
    customBackground: Int? = null,
    customOnBackground: Int? = null,
    customSurface: Int? = null,
    customOnSurface: Int? = null,
    customError: Int? = null,
    customOnError: Int? = null,
    customOutline: Int? = null,
    customDelete: Int? = null,
    customEdit: Int? = null,
    customComplete: Int? = null,
    customSelect: Int? = null,
    content: @Composable () -> Unit
) {
    val primary = customPrimary?.let { Color(it) } ?: AmoledDefault.Primary
    val onPrimary = customOnPrimary?.let { Color(it) } ?: AmoledDefault.OnPrimary

    val secondary = customSecondary?.let { Color(it) } ?: AmoledDefault.Secondary
    val onSecondary = customOnSecondary?.let { Color(it) } ?: AmoledDefault.OnSecondary

    val tertiary = customTertiary?.let { Color(it) } ?: AmoledDefault.Tertiary
    val onTertiary = customOnTertiary?.let { Color(it) } ?: AmoledDefault.OnTertiary

    val background = customBackground?.let { Color(it) } ?: AmoledDefault.Background
    val onBackground = customOnBackground?.let { Color(it) } ?: AmoledDefault.OnBackground

    val surface = customSurface?.let { Color(it) } ?: AmoledDefault.Surface
    val onSurface = customOnSurface?.let { Color(it) } ?: AmoledDefault.OnSurface

    val error = customError?.let { Color(it) } ?: AmoledDefault.Error
    val onError = customOnError?.let { Color(it) } ?: AmoledDefault.OnError

    val outline = customOutline?.let { Color(it) } ?: AmoledDefault.Outline

    val delete = customDelete?.let { Color(it) } ?: AmoledDefault.Delete
    val edit = customEdit?.let { Color(it) } ?: AmoledDefault.Edit
    val complete = customComplete?.let { Color(it) } ?: AmoledDefault.Complete
    val select = customSelect?.let { Color(it) } ?: AmoledDefault.Select


    val extraColors = ExtraColors(delete, edit, complete, select)

    val colorScheme = generateColorScheme(
        primary = primary,
        onPrimary = onPrimary,
        secondary = secondary,
        onSecondary = onSecondary,
        tertiary = tertiary,
        onTertiary = onTertiary,
        background = background,
        onBackground = onBackground,
        surface = surface,
        onSurface = onSurface,
        error = error,
        onError = onError,
        outline = outline
    )

    CompositionLocalProvider(LocalExtraColors provides extraColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
