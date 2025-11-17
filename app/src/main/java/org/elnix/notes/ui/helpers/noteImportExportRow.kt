package org.elnix.notes.ui.helpers

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import org.elnix.notes.R
import org.elnix.notes.ui.theme.AppObjectsColors
import org.elnix.notes.utils.NotesBackupManager

@Composable
fun NotesExportImportRow(
    onError: (Boolean, String) -> Unit,
    onSuccess: (Boolean) -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val exportLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
            if (uri == null) {
                onError(true, ctx.getString(R.string.export_cancelled))
                return@rememberLauncherForActivityResult
            }

            scope.launch {
                try {
                    ctx.contentResolver.openOutputStream(uri)?.use {
                        NotesBackupManager.exportNotes(ctx, it)
                    } ?: throw Exception("Unable to open output stream")

                    onSuccess(true)
                } catch (e: Exception) {
                    onError(true, e.message ?: ctx.getString(R.string.export_failed))
                }
            }
        }

    val importLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri == null) {
                onError(false, ctx.getString(R.string.import_cancelled))
                return@rememberLauncherForActivityResult
            }

            scope.launch {
                try {
                    ctx.contentResolver.openInputStream(uri)?.use {
                        NotesBackupManager.importNotes(ctx, it)
                    } ?: throw Exception("Unable to open input stream")

                    onSuccess(false)
                } catch (e: Exception) {
                    onError(false, e.message ?: ctx.getString(R.string.import_failed))
                }
            }
        }

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = { exportLauncher.launch("notes_backup.json") },
            colors = AppObjectsColors.buttonColors()
        ) {
            Text(stringResource(R.string.export_notes))
        }

        Button(
            onClick = { importLauncher.launch(arrayOf("application/json")) },
            colors = AppObjectsColors.buttonColors()
        ) {
            Text(stringResource(R.string.import_notes))
        }
    }
}
