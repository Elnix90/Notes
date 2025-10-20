package org.elnix.notes.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ColorScheme = darkColorScheme(
    primary = Purple40,
    background = Color.Black,
    onBackground = Color.White
)

@Composable
fun NotesTheme(
    customPrimary: Int? = null,
    customBackground: Int? = null,
    customOnBackground: Int? = null,
    content: @Composable () -> Unit
) {

    // pick base color scheme (dynamic/light/dark)
    var colorScheme = ColorScheme

    // Apply custom overrides (if set in settings)
    colorScheme = colorScheme.copy(
        primary = customPrimary?.let { Color(it) } ?: colorScheme.primary,
        background = customBackground?.let { Color(it) } ?: colorScheme.background,
        onBackground = customOnBackground?.let { Color(it) } ?: colorScheme.onBackground
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
