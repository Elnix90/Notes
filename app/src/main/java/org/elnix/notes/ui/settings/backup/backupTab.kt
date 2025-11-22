package org.elnix.notes.ui.settings.backup

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.launch
import org.elnix.notes.R
import org.elnix.notes.ui.helpers.TextDivider
import org.elnix.notes.ui.helpers.UserValidation
import org.elnix.notes.ui.helpers.settings.SettingsLazyHeader
import org.elnix.notes.ui.theme.AppObjectsColors
import org.elnix.notes.utils.NotesBackupManager
import org.elnix.notes.utils.SettingsBackupManager

@Composable
fun BackupTab(
    activity: FragmentActivity,
    onBack: () -> Unit
) {
//    val activity = LocalContext.current
    val scope = rememberCoroutineScope()

    // UI feedback state
    var showValidation by remember { mutableStateOf(false) }
    var isExport by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    fun showResult(export: Boolean, error: Boolean, message: String = "") {
        isExport = export
        isError = error
        errorMessage = message
        showValidation = true
    }

    // ------------------------------------------------------------
    // NOTES BACKUP
    // ------------------------------------------------------------

    val notesExportLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
            Log.d("NotesBackupManager", "Started notes export 2")
            if (uri == null) {
                showResult(
                    export = true,
                    error = true,
                    message = activity.getString(R.string.export_cancelled)
                )
                return@rememberLauncherForActivityResult
            }

            scope.launch {
                try {
                    activity.contentResolver.openOutputStream(uri, "w")?.use {
                        NotesBackupManager.exportNotes(activity, it)
                    } ?: throw Exception("Unable to open output stream")

                    showResult(export = true, error = false)

                } catch (e: Exception) {
                    showResult(export = true, error = true, message = e.message ?: "")
                }
            }
        }

    val notesImportLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            Log.d("NotesBackupManager", "Started notes import 2")
            if (uri == null) {
                showResult(
                    export = false,
                    error = true,
                    message = activity.getString(R.string.import_cancelled)
                )
                return@rememberLauncherForActivityResult
            }

            scope.launch {
                try {
                    activity.contentResolver.openInputStream(uri)?.use {
                        NotesBackupManager.importNotes(activity, it)
                    } ?: throw Exception("Unable to open input stream")

                    showResult(export = false, error = false)

                } catch (e: Exception) {
                    showResult(export = false, error = true, message = e.message ?: "")
                }
            }
        }

    // ------------------------------------------------------------
    // SETTINGS BACKUP
    // ------------------------------------------------------------

    val settingsExportLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
            Log.d("NotesBackupManager", "Started settings export 2")
            if (uri == null) {
                showResult(
                    export = true,
                    error = true,
                    message = activity.getString(R.string.export_cancelled)
                )
                return@rememberLauncherForActivityResult
            }

            scope.launch {
                try {
                    SettingsBackupManager.exportSettings(activity, uri)
                    showResult(export = true, error = false)
                } catch (e: Exception) {
                    showResult(export = true, error = true, message = e.message ?: "")
                }
            }
        }

    val settingsImportLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            Log.d("NotesBackupManager", "Started settings import 2")
            if (uri == null) {
                showResult(
                    export = false,
                    error = true,
                    message = activity.getString(R.string.import_cancelled)
                )
                return@rememberLauncherForActivityResult
            }

            scope.launch {
                try {
                    SettingsBackupManager.importSettings(activity, uri, activity)
                    showResult(export = false, error = false)
                } catch (e: Exception) {
                    showResult(export = false, error = true, message = e.message ?: "")
                }
            }
        }

    // ------------------------------------------------------------
    // UI
    // ------------------------------------------------------------

    SettingsLazyHeader(
        title = stringResource(R.string.backup_restore),
        onBack = onBack,
        helpText = stringResource(R.string.color_selector_text),
        resetText = null,
        onReset = null
    ) {
        Log.d("NotesBackupManager", "Initialized")

        // NOTES SECTION
        item { TextDivider(stringResource(R.string.notes_backup_restore)) }
        item {
            BackupButtons(
                exportLabel = stringResource(R.string.export_notes),
                importLabel = stringResource(R.string.import_notes),
                onExport = {
                    Log.d("NotesBackupManager", "Started notes export 1")
                    Log.d("NotesBackupManager", notesImportLauncher.toString())
                    notesExportLauncher.launch("notes_backup.json")
                },
                onImport = {
                    Log.d("NotesBackupManager", "Started notes import 1")
                    notesImportLauncher.launch(arrayOf("application/json"))
                }
            )
        }

        // SETTINGS SECTION
        item { TextDivider(stringResource(R.string.settings_backup_restore)) }
        item {
            BackupButtons(
                exportLabel = stringResource(R.string.export_settings),
                importLabel = stringResource(R.string.import_settings),
                onExport = {
                    Log.d("NotesBackupManager", "Started settings export 1")
                    settingsExportLauncher.launch("notes_settings_backup.json")
                },
                onImport = {
                    Log.d("NotesBackupManager", "Started settings import 1")
                    settingsImportLauncher.launch(arrayOf("application/json"))
                }
            )
        }
        item { Button(
            onClick = { showResult(true, true, "test") }
        ) { Text("test")} }
    }

    // RESULT DIALOG
    if (showValidation) {
        UserValidation(
            title = when {
                isError && isExport -> stringResource(R.string.export_failed)
                isError && !isExport -> stringResource(R.string.import_failed)
                !isError && isExport -> stringResource(R.string.export_successful)
                else -> stringResource(R.string.import_successful)
            },
            message = when {
                isError -> errorMessage.ifBlank { stringResource(R.string.unknown_error) }
                isExport -> stringResource(R.string.export_successful)
                else -> stringResource(R.string.import_successful)
            },
            titleIcon = if (isError) Icons.Default.Warning else Icons.Default.Check,
            titleColor = if (isError) MaterialTheme.colorScheme.error else Color.Green,
            cancelText = null,
            copy = isError,
            onCancel = { showValidation = false },
            onAgree = { showValidation = false }
        )
    }
}

// ------------------------------------------------------------
// Shared Buttons (internal)
// ------------------------------------------------------------

@Composable
fun BackupButtons(
    exportLabel: String,
    importLabel: String,
    onExport: () -> Unit,
    onImport: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = onExport,
            colors = AppObjectsColors.buttonColors()
        ) { Text(exportLabel) }

        Button(
            onClick = onImport,
            colors = AppObjectsColors.buttonColors()
        ) { Text(importLabel) }
    }
}
