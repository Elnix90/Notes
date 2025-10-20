// file: org/elnix/notes/NotesScreen.kt
package org.elnix.notes

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.elnix.notes.data.NoteEntity
import org.elnix.notes.ui.NoteViewModel
import org.elnix.notes.ui.theme.blendWith
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotesScreen(vm: NoteViewModel, navController: androidx.navigation.NavHostController) {
    val notes by vm.notes.collectAsState()
    val ctx = LocalContext.current

    if (notes.isEmpty()) {
        // Show placeholder message when no notes exist
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No notes yet.\nTap + to create one!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(notes) { note ->
                NoteCard(
                    note = note,
                    onClick = { navController.navigate("edit/${note.id}") },
                    onDelete = {
                        vm.delete(note)
                        Toast.makeText(ctx, "Deleted", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}


@Composable
fun NoteCard(note: NoteEntity, onClick: () -> Unit, onDelete: () -> Unit) {
    val shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    Card(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .fillMaxWidth(),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background.blendWith(
            MaterialTheme.colorScheme.primary, 0.2f)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(note.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(Modifier.height(6.dp))
                Text(text = note.title, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
