package org.elnix.notes.ui.settings

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import org.elnix.notes.data.settings.ColorSettingsStore.getBackgroundFlow
import org.elnix.notes.data.settings.ColorSettingsStore.getOnBackgroundFlow
import org.elnix.notes.data.settings.ColorSettingsStore.getPrimaryFlow
import org.elnix.notes.data.settings.ShowNavBarActions
import org.elnix.notes.data.settings.UiSettingsStore.getShowBottomNavLabelsFlow
import org.elnix.notes.ui.helpers.ExportImportRow
import org.elnix.notes.ui.helpers.NotesExportImportRow
import org.elnix.notes.ui.helpers.SettingsTitle

@Composable
fun BackupTab(ctx: Context, onBack: (() -> Unit)) {
    val primary by getPrimaryFlow(ctx).collectAsState(initial = null)
    val background by getBackgroundFlow(ctx).collectAsState(initial = null)
    val onBackground by getOnBackgroundFlow(ctx).collectAsState(initial = null)
    val showNavLabels by getShowBottomNavLabelsFlow(ctx).collectAsState(initial = ShowNavBarActions.ALWAYS)

    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SettingsTitle("Backup / Restore", onBack)

        ExportImportRow(
            ctx = ctx,
            primaryColor = primary ?: MaterialTheme.colorScheme.primary.toArgb(),
            backgroundColor = background ?: MaterialTheme.colorScheme.background.toArgb(),
            onBackgroundColor = onBackground ?: MaterialTheme.colorScheme.onBackground.toArgb(),
            showNavBarLabels = showNavLabels
        )
        NotesExportImportRow(ctx)

        Button(onClick = { error("Test crash") }) {
            Text("Crash test")
        }

    }
}
