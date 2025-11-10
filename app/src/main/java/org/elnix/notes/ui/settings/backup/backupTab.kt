package org.elnix.notes.ui.settings.backup

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import org.elnix.notes.R
import org.elnix.notes.ui.helpers.ExportImportRow
import org.elnix.notes.ui.helpers.NotesExportImportRow
import org.elnix.notes.ui.settings.SettingsLazyHeader

@Composable
fun BackupTab(ctx: Context, onBack: (() -> Unit)) {

    SettingsLazyHeader(
        title = stringResource(R.string.security_privacy),
        onBack = onBack
    ) {
        item { ExportImportRow() }
        item { NotesExportImportRow(ctx) }
    }
}
