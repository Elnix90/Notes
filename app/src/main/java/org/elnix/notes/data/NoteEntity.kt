// file: org/elnix/notes/data/NoteEntity.kt
package org.elnix.notes.data

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.elnix.notes.data.helpers.NoteType
import org.elnix.notes.ui.theme.AmoledDefault
import java.time.Instant
import java.util.Date

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val desc: String,
    val bgColor: Color = AmoledDefault.Surface,
    val txtColor: Color = AmoledDefault.OnSurface,
    val createdAt: Date = Date.from(Instant.now()),
    val isCompleted: Boolean = false,
    val type: NoteType = NoteType.TEXT
)
