package org.elnix.notes.ui.helpers


import androidx.compose.ui.graphics.Color
import kotlin.random.Random

fun randomColor(maxLuminance: Float = 1f, alpha: Boolean = false): Color {
    val hue = Random.nextFloat() * 360f
    val saturation = Random.nextFloat()
    val value = Random.nextFloat() * maxLuminance

    // Convert HSV to RGB
    val c = value * saturation
    val x = c * (1 - kotlin.math.abs((hue / 60f) % 2 - 1))
    val m = value - c

    val (r1, g1, b1) = when (hue) {
        in 0f..60f -> Triple(c, x, 0f)
        in 60f..120f -> Triple(x, c, 0f)
        in 120f..180f -> Triple(0f, c, x)
        in 180f..240f -> Triple(0f, x, c)
        in 240f..300f -> Triple(x, 0f, c)
        else -> Triple(c, 0f, x)
    }

    val r = r1 + m
    val g = g1 + m
    val b = b1 + m

    return Color(r, g, b, if (alpha) Random.nextFloat() else 1f)
}
