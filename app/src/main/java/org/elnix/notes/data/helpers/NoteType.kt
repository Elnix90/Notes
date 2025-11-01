package org.elnix.notes.data.helpers

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.elnix.notes.ui.theme.LocalExtraColors

enum class NoteType {
    TEXT, CHECKLIST, DRAWING
}

fun noteTypeIcon(type: NoteType) = when (type) {
    NoteType.TEXT -> Icons.Default.Edit
    NoteType.CHECKLIST -> Icons.AutoMirrored.Filled.FormatListBulleted
    NoteType.DRAWING -> Icons.Default.Add
}

@Composable
fun noteTypeColor(type: NoteType): Color {
    val extras = LocalExtraColors.current
    return when (type) {
        NoteType.TEXT -> extras.noteTypeText
        NoteType.DRAWING -> extras.noteTypeDrawing
        NoteType.CHECKLIST -> extras.noteTypeChecklist
    }
}