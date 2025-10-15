package org.elnix.notes.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF3B82F6),      // soft blue (buttons, highlights)
    onPrimary = Color.White,          // white text on blue
    secondary = Color(0xFF6366F1),    // violet accent
    onSecondary = Color.White,
    background = Color.White,   // warm off-white background
    onBackground = Color(0xFF1F2937), // dark gray text
    surface = Color(0xFFFFFFFF),      // card backgrounds
    onSurface = Color(0xFF1F2937),    // text on cards
    inversePrimary = Color(0xFFE0E7FF) // pale blue for inverse UI elements
)


private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF60A5FA),       // lighter blue
    onPrimary = Color.Black,           // readable text on bright surfaces
    secondary = Color(0xFFA78BFA),     // soft violet accent
    onSecondary = Color.Black,
    background = Color.Black,    // dark slate background
    onBackground = Color(0xFFE2E8F0),  // light gray text
    surface = Color(0xFF1E293B),       // slightly lighter cards
    onSurface = Color(0xFFE2E8F0),     // text on cards
    inversePrimary = Color(0xFF1D4ED8) // deep blue for special highlights
)


@Composable
fun NotesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}