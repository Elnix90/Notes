package org.elnix.notes.ui.settings.appearance

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

fun randomColor(alpha: Boolean = false) =
    Color(
        Random.nextFloat(),
        Random.nextFloat(),
        Random.nextFloat(),
        if (alpha) Random.nextFloat() else 1f
    )