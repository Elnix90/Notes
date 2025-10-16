package org.elnix.notes.utils

import androidx.compose.material3.DatePicker
import org.elnix.notes.data.NoteEntity
import java.util.Date
import java.time.Instant

fun getFakeNotes(): List<NoteEntity> {
    return listOf<NoteEntity>(
        NoteEntity(title = "test1", desc = "desc1", createdAt =  Date.from(Instant.now()), reminder = true, dueFor = Date.from(Instant.now())),
        NoteEntity(title = "test", desc = "desc1", createdAt =  Date.from(Instant.now()), reminder =  true, dueFor =  Date.from(Instant.now())),
        NoteEntity(title = "test15678777", desc = "desc1ggzsnsnngsfkngnzefnzqngzrogjojrtovntoze", createdAt =  Date.from(Instant.now()), reminder =  false, dueFor =  Date.from(Instant.now()))
    )
}