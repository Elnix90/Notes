package org.elnix.notes.ui.helpers

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.elnix.notes.Routes
import org.elnix.notes.data.helpers.NoteType

@Composable
fun AddNoteFab(navController: NavHostController) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        // Transparent overlay to detect clicks outside FABs
        if (expanded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { expanded = false }
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(16.dp)
        ) {
            if (expanded) {
                SmallFab(
                    icon = Icons.AutoMirrored.Filled.FormatListBulleted,
                    label = "Checklist",
                    color = MaterialTheme.colorScheme.secondary
                ) {
                    navController.navigate("${Routes.CREATE}?type=${NoteType.CHECKLIST.name}")
                    expanded = false
                }
                SmallFab(
                    icon = Icons.Default.Brush,
                    label = "Drawing",
                    color = MaterialTheme.colorScheme.tertiary
                ) {
                    navController.navigate("${Routes.CREATE}?type=${NoteType.DRAWING.name}")
                    expanded = false
                }
                SmallFab(
                    icon = Icons.Default.Edit,
                    label = "Text",
                    color = MaterialTheme.colorScheme.primary
                ) {
                    navController.navigate("${Routes.CREATE}?type=${NoteType.TEXT.name}")
                    expanded = false
                }
            } else {
                FloatingActionButton(
                    onClick = { expanded = !expanded },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add note"
                    )
                }
            }
        }
    }
}

@Composable
private fun SmallFab(icon: ImageVector, label: String, color: Color, onClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        FloatingActionButton(
            onClick = onClick,
            containerColor = color,
            modifier = Modifier.size(50.dp)
        ) {
            Icon(icon, contentDescription = label)
        }
    }
}
