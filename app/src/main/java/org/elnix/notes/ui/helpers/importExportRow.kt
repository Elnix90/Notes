package org.elnix.notes.ui.helpers

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import org.elnix.notes.R
import org.elnix.notes.ui.theme.AppObjectsColors
import org.elnix.notes.utils.SettingsBackupManager

@Composable
fun ExportImportRow() {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    // IMPORT
    val importLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri == null) return@rememberLauncherForActivityResult
            scope.launch {
                try {
                    SettingsBackupManager.importSettings(ctx, uri)
                    Toast.makeText(ctx, R.string.settings_imported_successfully, Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(ctx, "${ctx.getString(R.string.import_failed)}: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

    // EXPORT
    val exportLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
            if (uri == null) return@rememberLauncherForActivityResult
            scope.launch {
                try {
                    SettingsBackupManager.exportSettings(ctx, uri)
                    Toast.makeText(ctx, R.string.settings_exported_successfully, Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(ctx, "${ctx.getString(R.string.export_failed)}: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = { exportLauncher.launch("notes_settings_backup.json") },
            colors = AppObjectsColors.buttonColors()
        ) { Text(stringResource(R.string.export_settings)) }

        Button(
            onClick = { importLauncher.launch(arrayOf("application/json")) },
            colors = AppObjectsColors.buttonColors()
        ) { Text(stringResource(R.string.import_settings)) }
    }
}

