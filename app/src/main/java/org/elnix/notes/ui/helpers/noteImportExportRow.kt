package org.elnix.notes.ui.helpers

import android.content.Context
import android.util.Log
import android.widget.Toast
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
    val tag = "NotesExportImportRow"

    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri == null) {
            Toast.makeText(ctx, "Export cancelled", Toast.LENGTH_SHORT).show()
            return@rememberLauncherForActivityResult
        }

        scope.launch {
            try {
                ctx.contentResolver.openOutputStream(uri)?.use { NotesBackupManager.exportNotes(ctx, it) }
                    ?: throw Exception("Unable to open output stream")
            } catch (e: Exception) {
                Log.e(tag, "Export failed", e)
                Toast.makeText(ctx, "Export failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) {
            Toast.makeText(ctx, "Import cancelled", Toast.LENGTH_SHORT).show()
            return@rememberLauncherForActivityResult
        }

        scope.launch {
            try {
                ctx.contentResolver.openInputStream(uri)?.use { NotesBackupManager.importNotes(ctx, it) }
                    ?: throw Exception("Unable to open input stream")
            } catch (e: Exception) {
                Log.e(tag, "Import failed", e)
                Toast.makeText(ctx, "Import failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
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
