package org.elnix.notes.ui.settings

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.elnix.notes.R
import org.elnix.notes.ui.helpers.ExportImportRow
import org.elnix.notes.ui.helpers.NotesExportImportRow
import org.elnix.notes.ui.helpers.SettingsTitle

@Composable
fun BackupTab(ctx: Context, onBack: (() -> Unit)) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                WindowInsets.systemBars
                    .asPaddingValues()
            )
            .padding(horizontal = 16.dp, vertical = 5.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SettingsTitle(title = stringResource(R.string.verification), onBack = onBack)

        ExportImportRow()
        NotesExportImportRow(ctx)

    }
}
