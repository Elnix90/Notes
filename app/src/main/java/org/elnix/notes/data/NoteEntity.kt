// file: NoteEntity.kt
package org.elnix.notes.data

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.elnix.notes.data.helpers.NoteType
import java.time.Instant
import java.util.Calendar
import java.util.Date

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String = "",
    val desc: String = "",
    val checklist: List<ChecklistItem> = emptyList(),
    val tagIds: List<Long> = emptyList(),

    val bgColor: Color? = null,
    val txtColor: Color? = null,
    val autoTextColor: Boolean = true,

    val isCompleted: Boolean = false,
    val type: NoteType = NoteType.TEXT,

    val dueDateTime: Calendar? = null,
    val createdAt: Date = Date.from(Instant.now()),
    val lastEdit: Long = System.currentTimeMillis(),

    val orderIndex: Int = 0
)


data class ChecklistItem(
    val text: String,
    val checked: Boolean
)
