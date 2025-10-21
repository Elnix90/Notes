package org.elnix.notes.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance


fun generateColorScheme(
    primary: Color,
    background: Color,
    onBackground: Color
): ColorScheme {

    val base = darkColorScheme()

    val onPrimary = if (primary.luminance() > 0.5f) Color.Black else Color.White
    val surface = background.blendWith(primary, 0.2f)
    val onSurface = if (surface.luminance() > 0.5f) Color.Black else Color.White

    return base.copy(
        primary = primary,
        onPrimary = onPrimary,
        secondary = primary.adjustBrightness(1.5f),
        onSecondary = if (primary.luminance() > 0.5f) Color.Black else Color.White,
        background = background,
        onBackground = onBackground,
        surface = surface,
        onSurface = onSurface,
        error = Color.Red,
        onError = Color.White,
        primaryContainer = primary.blendWith(Color.Black, 0.1f),
        onPrimaryContainer = if (primary.luminance() > 0.5f) Color.Black else Color.White,
        secondaryContainer = primary.blendWith(Color.Gray, 0.2f),
        onSecondaryContainer = if (primary.luminance() > 0.5f) Color.Black else Color.White,
        tertiary = primary.blendWith(Color.Cyan, 0.3f),
        onTertiary = if (primary.luminance() > 0.5f) Color.Black else Color.White,
        surfaceVariant = background.blendWith(Color.Gray, 0.1f),
        onSurfaceVariant = if (background.luminance() > 0.5f) Color.Black else Color.White,
        outline = primary.blendWith(Color.White, 0.5f),
    )
}



@Composable
fun NotesTheme(
    customPrimary: Int? = null,
    customBackground: Int? = null,
    customOnBackground: Int? = null,
    content: @Composable () -> Unit
) {

    val primaryColor = customPrimary?.let { Color(it) } ?: Purple40
    val backgroundColor = customBackground?.let { Color(it) } ?: Color.Black
    val onBackgroundColor = customOnBackground?.let { Color(it) } ?: Color.White
    val colorScheme = generateColorScheme(primaryColor, backgroundColor, onBackgroundColor)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
