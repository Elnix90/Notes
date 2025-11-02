package org.elnix.notes.data.helpers

import androidx.compose.ui.graphics.Color


data class TagItem(
    val id: Long = System.currentTimeMillis(),
    val name: String,
    val color: Color,
    val selected: Boolean = true
)
