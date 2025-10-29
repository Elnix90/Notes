package org.elnix.notes.ui.settings

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.elnix.notes.data.settings.ShowNavBarActions
import org.elnix.notes.data.settings.stores.UiSettingsStore.getShowBottomNavLabelsFlow
import org.elnix.notes.ui.helpers.ExportImportRow
import org.elnix.notes.ui.helpers.NotesExportImportRow
import org.elnix.notes.ui.helpers.SettingsTitle

@Composable
fun BackupTab(ctx: Context, onBack: (() -> Unit)) {
    val showNavLabels by getShowBottomNavLabelsFlow(ctx).collectAsState(initial = ShowNavBarActions.ALWAYS)

    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SettingsTitle(title = "Backup / Restore", onBack = onBack)

        ExportImportRow(showNavLabels)
        NotesExportImportRow(ctx)

    }
}
