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
    content: @Composable () -> Unit
) {
    val primary = customPrimary?.let { Color(it) } ?: PrimaryDefault
    val onPrimary = customOnPrimary?.let { Color(it) } ?: OnPrimaryDefault

    val secondary = customSecondary?.let { Color(it) } ?: Secondary40
    val onSecondary = customOnSecondary?.let { Color(it) } ?: OnSecondaryDefault

    val tertiary = customTertiary?.let { Color(it) } ?: Tertiary40
    val onTertiary = customOnTertiary?.let { Color(it) } ?: OnTertiaryDefault

    val background = customBackground?.let { Color(it) } ?: BackgroundDefault
    val onBackground = customOnBackground?.let { Color(it) } ?: OnBackgroundDefault

    val surface = customSurface?.let { Color(it) } ?: SurfaceDefault
    val onSurface = customOnSurface?.let { Color(it) } ?: OnSurfaceDefault

    val error = customError?.let { Color(it) } ?: ErrorDefault
    val onError = customOnError?.let { Color(it) } ?: OnErrorDefault

    val outline = customOutline?.let { Color(it) } ?: OutlineDefault

    val delete = customDelete?.let { Color(it) } ?: DeleteDefault
    val edit = customEdit?.let { Color(it) } ?: EditDefault
    val complete = customComplete?.let { Color(it) } ?: CompleteDefault

    val extraColors = ExtraColors(delete, edit, complete)

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
