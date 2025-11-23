package org.elnix.notes.ui.settings.backup

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.elnix.notes.R
import org.elnix.notes.ui.NoteViewModel
import org.elnix.notes.ui.helpers.TextDivider
import org.elnix.notes.ui.helpers.UserValidation
import org.elnix.notes.ui.helpers.settings.SettingsLazyHeader
import org.elnix.notes.ui.theme.AppObjectsColors
import org.elnix.notes.utils.NotesBackupManager
import org.elnix.notes.utils.SettingsBackupManager

@Composable
fun BackupTab(
    vm: NoteViewModel,
    activity: FragmentActivity,
    onBack: () -> Unit
) {
    val scope = activity.lifecycleScope

    val backupVm by activity.viewModels<BackupViewModel>()
    val result by backupVm.result.collectAsState()

    // ------------------------------------------------------------
    // NOTES BACKUP
    // ------------------------------------------------------------

    val notesExportLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
            Log.d("NotesBackupManager", "Started notes export 2")

            if (uri == null) {
                backupVm.setResult(
                    BackupResult(
                        export = true,
                        error = true,
                        message = activity.getString(R.string.export_cancelled)
                    )
                )
                return@rememberLauncherForActivityResult
            }

            scope.launch {
                try {
                    activity.contentResolver.openOutputStream(uri, "w")?.use {
                        NotesBackupManager.exportNotes(activity, it)
                    } ?: throw Exception("Unable to open output stream")

                    backupVm.setResult(BackupResult(export = true, error = false))

                } catch (e: Exception) {
                    backupVm.setResult(
                        BackupResult(
                            export = true,
                            error = true,
                            message = e.message ?: ""
                        )
                    )
                }
            }
        }

    val notesImportLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            Log.d("NotesBackupManager", "Started notes import 2")

            if (uri == null) {
                backupVm.setResult(
                    BackupResult(
                        export = false,
                        error = true,
                        message = activity.getString(R.string.import_cancelled)
                    )
                )
                return@rememberLauncherForActivityResult
            }

            scope.launch {
                try {
                    activity.contentResolver.openInputStream(uri)?.use {
                        NotesBackupManager.importNotes(activity, it)
                    } ?: throw Exception("Unable to open input stream")

                    backupVm.setResult(BackupResult(export = false, error = false))

                } catch (e: Exception) {
                    backupVm.setResult(
                        BackupResult(
                            export = false,
                            error = true,
                            message = e.message ?: ""
                        )
                    )
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
                backupVm.setResult(
                    BackupResult(
                        export = true,
                        error = true,
                        message = activity.getString(R.string.export_cancelled)
                    )
                )
                return@rememberLauncherForActivityResult
            }

            scope.launch {
                try {
                    SettingsBackupManager.exportSettings(activity, uri)
                    backupVm.setResult(BackupResult(export = true, error = false))

                } catch (e: Exception) {
                    backupVm.setResult(
                        BackupResult(
                            export = true,
                            error = true,
                            message = e.message ?: ""
                        )
                    )
                }
            }
        }

    val settingsImportLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            Log.d("NotesBackupManager", "Started settings import 2")

            if (uri == null) {
                backupVm.setResult(
                    BackupResult(
                        export = false,
                        error = true,
                        message = activity.getString(R.string.import_cancelled)
                    )
                )
                return@rememberLauncherForActivityResult
            }

            scope.launch {
                try {
                    SettingsBackupManager.importSettings(activity, uri, activity)
                    backupVm.setResult(BackupResult(export = false, error = false))

                } catch (e: Exception) {
                    backupVm.setResult(
                        BackupResult(
                            export = false,
                            error = true,
                            message = e.message ?: ""
                        )
                    )
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
                    vm.enableIgnoreBackgroundLock()
                    notesExportLauncher.launch("notes_backup.json")
                },
                onImport = {
                    vm.enableIgnoreBackgroundLock()
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
                    vm.enableIgnoreBackgroundLock()
                    settingsExportLauncher.launch("notes_settings_backup.json")
                },
                onImport = {
                    vm.enableIgnoreBackgroundLock()
                    settingsImportLauncher.launch(arrayOf("application/json"))
                }
            )
        }
    }

    // ------------------------------------------------------------
    // RESULT DIALOG
    // ------------------------------------------------------------

    if (result != null) {
        val isError = result!!.error
        val isExport = result!!.export
        val errorMessage = result!!.message

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
            onCancel = {},
            onAgree = { backupVm.setResult(null) }
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
