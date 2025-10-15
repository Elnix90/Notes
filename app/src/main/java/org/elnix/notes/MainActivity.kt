package org.elnix.notes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.elnix.notes.ui.theme.NotesTheme
import org.elnix.notes.utils.Note
import org.elnix.notes.utils.getFakeNotes

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotesTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)

                )
                {
                    Column {
                        NoteList(getFakeNotes())
                        ColorPreview()
                    }
                }
            }
        }
    }
}




@Composable
fun NoteItem(item: Note) {
    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp) // outer spacing between cards
            .clip(shape)
            .background(MaterialTheme.colorScheme.inversePrimary)
            .padding(16.dp) // inner padding for content
    ) {
        Column {
            // Date (small & dim)
            Text(
                text = item.createdAt.toString(),
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

@Composable
fun ColorPreview() {
    val colors = MaterialTheme.colorScheme
    Column {
        listOf(
            "primary" to colors.primary,
            "onPrimary" to colors.onPrimary,
            "inversePrimary" to colors.inversePrimary,
            "surface" to colors.surface,
            "onSurface" to colors.onSurface,
            "background" to colors.background,
            "onBackground" to colors.onBackground
        ).forEach { (name, color) ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(color)
            ) {
                Text(name, color = Color.White, modifier = Modifier.padding(8.dp))
            }
        }
    }
}