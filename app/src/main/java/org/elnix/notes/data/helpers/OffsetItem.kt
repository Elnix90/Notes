package org.elnix.notes.data.helpers

data class OffsetItem (
    val id: Long = System.currentTimeMillis(),
    val offset: Int
)