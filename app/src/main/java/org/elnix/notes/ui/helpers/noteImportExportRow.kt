package org.elnix.notes.ui.helpers

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.elnix.notes.ui.theme.AppObjectsColors
import org.elnix.notes.utils.NotesBackupManager

@Composable
fun NotesExportImportRow(ctx: Context) {
    val scope = rememberCoroutineScope()

    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        uri?.let {
            scope.launch {
                ctx.contentResolver.openOutputStream(uri)?.use { NotesBackupManager.exportNotes(ctx, it) }
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            scope.launch {
                ctx.contentResolver.openInputStream(uri)?.use { NotesBackupManager.importNotes(ctx, it) }
            }
        }
    }

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(onClick = { exportLauncher.launch("notes_backup.json") }, colors = AppObjectsColors.buttonColors()) {
            Text("Export Notes")
        }
        Button(onClick = { importLauncher.launch(arrayOf("application/json")) }, colors = AppObjectsColors.buttonColors()) {
            Text("Import Notes")
        }
    }
}
