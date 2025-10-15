package org.elnix.notes.utils

import androidx.compose.material3.DatePicker
import java.util.Date
import java.time.Instant

fun getFakeNotes(): List<Note> {
    return listOf<Note>(
        Note("test1","desc1", Date.from(Instant.now()),true, Date.from(Instant.now())),
        Note("test","desc1", Date.from(Instant.now()),true, Date.from(Instant.now())),
        Note("test15678777","desc1ggzsnsnngsfkngnzefnzqngzrogjojrtovntoze", Date.from(Instant.now()),false, Date.from(Instant.now()))
    )
}