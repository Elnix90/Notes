package org.elnix.notes.ui.settings.debug

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.elnix.notes.data.AppDatabase
import org.elnix.notes.ui.NoteViewModel
import org.elnix.notes.ui.helpers.TextDivider
import org.elnix.notes.ui.helpers.UserValidation
import org.elnix.notes.ui.helpers.settings.SettingsLazyHeader
import org.elnix.notes.ui.theme.AppObjectsColors

@Composable
fun NotesDebugTab(
    vm: NoteViewModel,
    scope: CoroutineScope,
    onBack: (() -> Unit)
) {
    var showConfirmDeleteAllNotes by remember { mutableStateOf(false) }

    val ctx = LocalContext.current

    SettingsLazyHeader(
        title = "Debug -> Notes",
        onBack = onBack,
        helpText = "Debug, too busy to make a translated explanation",
        onReset = null,
        resetText = null
    ) {

        item {
            Button(
                onClick = { scope.launch { vm.createFakeNotes(10) } },
                modifier = Modifier.fillMaxWidth(),
                colors = AppObjectsColors.buttonColors()
            ) {
                Text("Create 10 fake notes", color = MaterialTheme.colorScheme.onPrimary)
            }
        }

        item {
            TextDivider(
                text = "Danger Zone",
                lineColor = MaterialTheme.colorScheme.error,
                textColor = MaterialTheme.colorScheme.error
            )
        }

        item { RequestCreateManyNotesButon(100, scope, vm) }

        item { RequestCreateManyNotesButon(1000, scope, vm) }

        item { RequestCreateManyNotesButon(100000, scope, vm) }

        item {
            OutlinedButton(
                onClick = { showConfirmDeleteAllNotes = true },
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                colors = AppObjectsColors.cancelButtonColors()
            ) {
                Text(
                    text = "Delete All Notes",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
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


@Composable
fun RequestCreateManyNotesButon(
    noteNumber: Int,
    scope: CoroutineScope,
    vm: NoteViewModel
) {
    var showConfirm by remember { mutableStateOf(false) }

    OutlinedButton(
        onClick = { showConfirm = true },
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
        colors = AppObjectsColors.cancelButtonColors()
    ) {
        Text(
            text = "Create $noteNumber notes",
            color = MaterialTheme.colorScheme.error
        )
    }


    if (showConfirm) {
        UserValidation(
            message = "You are about to create $noteNumber fake notes.\nThis may crash your device.",
            title = "Are you sure?",
            onCancel = { showConfirm = false },
            onAgree = {
                showConfirm = false
                scope.launch{ vm.createFakeNotes(noteNumber) }
            }
        )
    }
}
