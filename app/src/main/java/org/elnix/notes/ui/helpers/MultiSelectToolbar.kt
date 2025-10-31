package org.elnix.notes.ui.helpers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.elnix.notes.data.settings.SwipeActions

@Composable
fun MultiSelectToolbar(onGroupAction: (SwipeActions) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        IconButton(onClick = { onGroupAction(SwipeActions.DELETE) }) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Selected")
        }
        IconButton(onClick = { onGroupAction(SwipeActions.COMPLETE) }) {
            Icon(imageVector = Icons.Default.CheckBox, contentDescription = "Mark Complete")
        }
    }
}
