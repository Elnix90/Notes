package org.elnix.notes.ui.editors

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.notes.data.ChecklistItem
import org.elnix.notes.data.NoteEntity
import org.elnix.notes.data.helpers.NoteType
import org.elnix.notes.ui.NoteViewModel
import org.elnix.notes.ui.helpers.colors.ColorPickerRow

@Composable
fun ChecklistEditorScreen(
    vm: NoteViewModel,
    noteId: Long?,
    onSaved: () -> Unit,
    onCancel: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var note by remember { mutableStateOf<NoteEntity?>(null) }

    LaunchedEffect(noteId) {
        note = noteId?.let { vm.getById(it) }
            ?: NoteEntity(type = NoteType.CHECKLIST)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val updated = (note?.checklist ?: emptyList()) + ChecklistItem("", false)
                note = note?.copy(checklist = updated)
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add item")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = note?.title ?: "",
                onValueChange = { t -> note = note?.copy(title = t) },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val list = note?.checklist ?: emptyList()
                itemsIndexed(list) { i, item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = item.checked,
                            onCheckedChange = { checked ->
                                note = note?.copy(
                                    checklist = list.toMutableList().apply {
                                        this[i] = item.copy(checked = checked)
                                    }
                                )
                            }
                        )
                        OutlinedTextField(
                            value = item.text,
                            onValueChange = { txt ->
                                note = note?.copy(
                                    checklist = list.toMutableList().apply {
                                        this[i] = item.copy(text = txt)
                                    }
                                )
                            },
                            label = { Text("Item ${i + 1}") },
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = {
                            note = note?.copy(
                                checklist = list.toMutableList().apply { removeAt(i) }
                            )
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Remove")
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            ColorPickerRow(
                label = "Background",
                showLabel = true,
                defaultColor = MaterialTheme.colorScheme.surface,
                currentColor = note?.bgColor?.value?.toInt() ?: 0,
                scope = scope,
                backgroundColor = MaterialTheme.colorScheme.background
            ) { pickedInt ->
                note = note?.copy(bgColor = Color(pickedInt))
            }

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = {
                    scope.launch {
                        note?.let { vm.update(it.copy(lastEdit = System.currentTimeMillis())) }
                        onSaved()
                    }
                }) { Text("Save") }

                OutlinedButton(onClick = onCancel) { Text("Cancel") }
            }
        }
    }
}
