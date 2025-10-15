package org.elnix.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.elnix.notes.utils.Note
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun NoteItem(item: Note) {
    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp) // outer spacing between cards
            .clip(shape)
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp) // inner padding for content
    ) {
        Column {
            // Date (small & dim)
            Text(
                text = SimpleDateFormat("MM/dd", Locale.FRANCE).format(item.createdAt),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
            )

            // Main note text (bigger & brighter)
            Text(
                text = item.title,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}




@Composable
fun NoteList(items: List<Note>) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
    ) {
        LazyColumn (
            content = {
                items(items){ item ->
                    NoteItem(item)
                }
            }
        )
    }
}
