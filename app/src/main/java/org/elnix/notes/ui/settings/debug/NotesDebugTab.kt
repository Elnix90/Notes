package org.elnix.notes.ui.settings.debug

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.elnix.notes.data.AppDatabase
import org.elnix.notes.ui.NoteViewModel
import org.elnix.notes.ui.helpers.SettingsTitle
import org.elnix.notes.ui.helpers.UserValidation
import org.elnix.notes.ui.theme.AppObjectsColors

@Composable
fun NotesDebugTab(vm: NoteViewModel, onBack: (() -> Unit)) {
    var showConfirmCreate1000Notes by remember { mutableStateOf(false) }
    var showConfirmDeleteAllNotes by remember { mutableStateOf(false) }

    val ctx = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SettingsTitle("Debug -> Notes", onBack)

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

            Button(
                onClick = { vm.createFakeNotes(10) },
                modifier = Modifier.fillMaxWidth(),
                colors = AppObjectsColors.buttonColors()
            ) {
                Text("Create 10 fake notes", color = MaterialTheme.colorScheme.onBackground)
            }

            OutlinedButton(
                onClick = { vm.createFakeNotes(100) },
                modifier = Modifier.fillMaxWidth(),
                colors = AppObjectsColors.cancelButtonColors()
            ) {
                Text("Create 100 fake notes", color = MaterialTheme.colorScheme.error)
            }

            OutlinedButton(
                onClick = { vm.createFakeNotes(1000) },
                modifier = Modifier.fillMaxWidth(),
                colors = AppObjectsColors.cancelButtonColors()
            ) {
                Text("Create 1000 fake notes", color = MaterialTheme.colorScheme.error)
            }




            OutlinedButton(
                onClick = { showConfirmCreate1000Notes = true },
                modifier = Modifier.fillMaxWidth(),
                colors = AppObjectsColors.cancelButtonColors()
            ) {
                Text(
                    text = "Create 100 000 fake notes",
                    color = MaterialTheme.colorScheme.error
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline)

            OutlinedButton(
                onClick = { showConfirmDeleteAllNotes = true },
                modifier = Modifier.fillMaxWidth(),
                colors = AppObjectsColors.cancelButtonColors()
            ) {
                Text(
                    text = "Delete All Notes",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }


    if (showConfirmCreate1000Notes) {
        UserValidation(
            message = "You are about to create 100 000 fake notes.\nThis may crash your device.",
            title = "Are you sure?",
            onCancel = { showConfirmCreate1000Notes = false },
            onAgree = {
                showConfirmCreate1000Notes = false
                vm.createFakeNotes(100_000)
            }
        )
    }


    if (showConfirmDeleteAllNotes) {
        UserValidation(
            message = "You are about to delete all your notes, the app will crash.\nThis can't be undone.",
            title = "Are you sure?",
            onCancel = { showConfirmDeleteAllNotes = false },
            onAgree = {
                showConfirmDeleteAllNotes = false
                AppDatabase.reset(ctx)
                error("All Notes deleted")
            }
        )
    }
}

