package org.elnix.notes

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.elnix.notes.data.NoteEntity
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun NoteItem(
    item: NoteEntity,
    onActionClick: (() -> Unit)? = null // optional callback for the button
) {
    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp) // outer spacing between cards
            .clip(shape)
            .background(MaterialTheme.colorScheme.primary) // card background
            .padding(16.dp) // inner padding
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f) // take remaining space
            ) {
                // Date (small & dim)
                Text(
                    text = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                        .format(item.createdAt),
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

            // Optional action button (edit/delete)
            if (onActionClick != null) {
                IconButton(
                    onClick = onActionClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    // Example icon: a trash bin or edit icon
                    androidx.compose.material3.Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Delete,
                        contentDescription = "Delete Note",
                        tint = MaterialTheme.colorScheme.background
                    )
                }
            }
        }
    }
}




@Composable
fun NoteList(items: List<NoteEntity>) {
    val context = LocalContext.current
    Column (
        modifier = Modifier
            .fillMaxWidth()
    ) {
        LazyColumn (
            content = {
                items(items){ item ->
                    NoteItem(item) {
                        Toast.makeText(context, "Clicked!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }
}
