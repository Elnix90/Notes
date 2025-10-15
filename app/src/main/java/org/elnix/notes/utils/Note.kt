package org.elnix.notes.utils

import java.util.Date

data class Note(
    val title: String,
    val desc: String,
    val createdAt: Date,
    val reminder: Boolean,
    val dueFor: Date
)