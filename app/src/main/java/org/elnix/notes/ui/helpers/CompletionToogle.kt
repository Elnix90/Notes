package org.elnix.notes.ui.helpers

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.notes.R
import org.elnix.notes.data.NoteEntity
import org.elnix.notes.ui.NoteViewModel
import org.elnix.notes.ui.theme.AppObjectsColors

@Composable
fun CompletionToggle(
    note: NoteEntity?,
    currentId: Long?,
    vm: NoteViewModel,
    onUpdated: (NoteEntity) -> Unit
) {
    val scope = rememberCoroutineScope()
    var isCompleted by remember { mutableStateOf(note?.isCompleted ?: false) }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable {
                    isCompleted = !isCompleted
                    scope.launch {
                        currentId?.let { id ->
                            val n = vm.getById(id)
                            if (n != null) {
                                val updated = n.copy(isCompleted = isCompleted)
                                vm.update(updated)
                                onUpdated(updated)
                            }
                        }
                    }
                }
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.completed),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.width(12.dp))

            Checkbox(
                checked = isCompleted,
                onCheckedChange = null,
                colors = AppObjectsColors.checkboxColors()
            )
        }
    }
}
