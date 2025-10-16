// file: org/elnix/notes/EditNoteScreen.kt
package org.elnix.notes

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.elnix.notes.data.NoteEntity
import org.elnix.notes.ui.NoteViewModel
import kotlinx.coroutines.launch

@Composable
fun EditNoteScreen(noteId: Long, vm: NoteViewModel, onSaved: () -> Unit) {
    var note by remember { mutableStateOf<NoteEntity?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(noteId) {
        val n = vm.getById(noteId) // we need a function in vm to fetch single note
        note = n
    }

    if (note == null) {
        // loading / placeholder
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) { Text("Loading...") }
        return
    }

    var title by remember { mutableStateOf(note!!.title) }
    var desc by remember { mutableStateOf(note!!.desc) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth().height(200.dp))
        Spacer(Modifier.weight(1f))
        Button(onClick = {
            scope.launch {
                vm.update(note!!.copy(title = title, desc = desc))
                onSaved()
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Update")
        }
    }
}
