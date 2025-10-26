package org.elnix.notes.ui.helpers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.elnix.notes.data.settings.Action

@Composable
fun ActionSelectorRow(
    label: String,
    selected: Action,
    onSelected: (Action) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(
                onClick = { showDialog = true}
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)

    ) {


        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = selected.name,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            style = MaterialTheme.typography.labelSmall,
        )

    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {},
            dismissButton = {},
            title = {},
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Action.entries.forEach { action ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp)
                                .clickable {
                                    onSelected(action)
                                    showDialog = false
                                }
                        ) {
                            RadioButton(
                                selected = (selected == action),
                                onClick = {
                                    onSelected(action)
                                    showDialog = false
                                }
                            )
                            Text(
                                text = action.name,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
