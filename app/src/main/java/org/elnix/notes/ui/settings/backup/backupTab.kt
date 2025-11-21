package org.elnix.notes.ui.settings.backup

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.FragmentActivity
import org.elnix.notes.R
import org.elnix.notes.ui.helpers.ExportImportRow
import org.elnix.notes.ui.helpers.NotesExportImportRow
import org.elnix.notes.ui.helpers.TextDivider
import org.elnix.notes.ui.helpers.UserValidation
import org.elnix.notes.ui.helpers.settings.SettingsLazyHeader

@Composable
fun BackupTab(
    activity: FragmentActivity,
    onBack: (() -> Unit)
) {

    // UI state for showing dialogs
    var showValidation by remember { mutableStateOf(false) }
    var isExport by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    SettingsLazyHeader(
        title = stringResource(R.string.backup_restore),
        onBack = onBack,
        helpText = stringResource(R.string.color_selector_text),
        resetText = null,
        onReset = null
    ) {
        // NOTES backup section
        item { TextDivider(stringResource(R.string.notes_backup_restore)) }
        item {
            NotesExportImportRow(
                onError = { export, message ->
                    isExport = export
                    isError = true
                    errorMessage = message
                    showValidation = true
                },
                onSuccess = { export ->
                    isExport = export
                    isError = false
                    showValidation = true
                }
            )
        }

        // SETTINGS backup section
        item { TextDivider(stringResource(R.string.settings_backup_restore)) }
        item {
            ExportImportRow(
                activity,
                onError = { export, message ->
                    isExport = export
                    isError = true
                    errorMessage = message
                    showValidation = true
                },
                onSuccess = { export ->
                    isExport = export
                    isError = false
                    showValidation = true
                }
            )
        }
    }

    if (showValidation) {
        UserValidation(
            title = when {
                isError && isExport  -> stringResource(R.string.export_failed)
                isError && !isExport -> stringResource(R.string.import_failed)
                !isError && isExport -> stringResource(R.string.export_successful)
                else                 -> stringResource(R.string.import_successful)
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
