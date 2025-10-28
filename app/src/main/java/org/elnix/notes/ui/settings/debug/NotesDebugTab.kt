package org.elnix.notes.ui.settings.debug

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.elnix.notes.ui.NoteViewModel
import org.elnix.notes.ui.helpers.SettingsTitle
import org.elnix.notes.ui.helpers.UserValidation
import org.elnix.notes.ui.theme.AppObjectsColors

@Composable
fun NotesDebugTab(vm: NoteViewModel, onBack: (() -> Unit)) {
    var showConfirm by remember { mutableStateOf(false) }

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


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {


                OutlinedButton(
                    onClick = { showConfirm = true },
                    modifier = Modifier.weight(1f),
                    colors = AppObjectsColors.cancelButtonColors()
                ) {
                    Text(
                        text = "Create 100 000 fake notes",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }


    if (showConfirm) {
        UserValidation(
            message = "You are about to create 100 000 fake notes.\nThis may crash your device.",
            title = "Are you sure?",
            onCancel = { showConfirm = false },
            onAgree = {
                showConfirm = false
                vm.createFakeNotes(100_000)
            }
        )
    }
}

