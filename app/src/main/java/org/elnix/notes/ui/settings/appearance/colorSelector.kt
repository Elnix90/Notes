package org.elnix.notes.ui.settings.appearance

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.elnix.notes.data.SettingsStore
import org.elnix.notes.ui.helpers.ColorPickerRow
import org.elnix.notes.ui.helpers.SettingsTitle
import org.elnix.notes.ui.theme.AppObjectsColors

@Composable
fun ColorSelectorTab(
    ctx: Context,
    scope: CoroutineScope,
    onBack: (() -> Unit)
) {

    // === Collect all theme color states ===
    val primary by SettingsStore.getPrimaryFlow(ctx).collectAsState(initial = null)
    val onPrimary by SettingsStore.getOnPrimaryFlow(ctx).collectAsState(initial = null)

    val secondary by SettingsStore.getSecondaryFlow(ctx).collectAsState(initial = null)
    val onSecondary by SettingsStore.getOnSecondaryFlow(ctx).collectAsState(initial = null)

    val tertiary by SettingsStore.getTertiaryFlow(ctx).collectAsState(initial = null)
    val onTertiary by SettingsStore.getOnTertiaryFlow(ctx).collectAsState(initial = null)

    val background by SettingsStore.getBackgroundFlow(ctx).collectAsState(initial = null)
    val onBackground by SettingsStore.getOnBackgroundFlow(ctx).collectAsState(initial = null)

    val surface by SettingsStore.getSurfaceFlow(ctx).collectAsState(initial = null)
    val onSurface by SettingsStore.getOnSurfaceFlow(ctx).collectAsState(initial = null)

    val error by SettingsStore.getErrorFlow(ctx).collectAsState(initial = null)
    val onError by SettingsStore.getOnErrorFlow(ctx).collectAsState(initial = null)

    val outline by SettingsStore.getOutlineFlow(ctx).collectAsState(initial = null)

    val delete by SettingsStore.getDeleteFlow(ctx).collectAsState(initial = null)
    val edit by SettingsStore.getEditFlow(ctx).collectAsState(initial = null)
    val complete by SettingsStore.getCompleteFlow(ctx).collectAsState(initial = null)

    val scrollState = rememberScrollState()

    // === Layout ===
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SettingsTitle("Color Selector", onBack)

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

            // === Primary set ===
            ColorPickerRow(
                label = "Primary",
                currentColor = primary ?: MaterialTheme.colorScheme.primary.toArgb()
            ) { scope.launch { SettingsStore.setPrimary(ctx, it) } }

            ColorPickerRow(
                label = "On Primary",
                currentColor = onPrimary ?: MaterialTheme.colorScheme.onPrimary.toArgb()
            ) { scope.launch { SettingsStore.setOnPrimary(ctx, it) } }

            // === Secondary set ===
            ColorPickerRow(
                label = "Secondary",
                currentColor = secondary ?: MaterialTheme.colorScheme.secondary.toArgb()
            ) { scope.launch { SettingsStore.setSecondary(ctx, it) } }

            ColorPickerRow(
                label = "On Secondary",
                currentColor = onSecondary ?: MaterialTheme.colorScheme.onSecondary.toArgb()
            ) { scope.launch { SettingsStore.setOnSecondary(ctx, it) } }

            // === Tertiary set ===
            ColorPickerRow(
                label = "Tertiary",
                currentColor = tertiary ?: MaterialTheme.colorScheme.tertiary.toArgb()
            ) { scope.launch { SettingsStore.setTertiary(ctx, it) } }

            ColorPickerRow(
                label = "On Tertiary",
                currentColor = onTertiary ?: MaterialTheme.colorScheme.onTertiary.toArgb()
            ) { scope.launch { SettingsStore.setOnTertiary(ctx, it) } }

            // === Background ===
            ColorPickerRow(
                label = "Background",
                currentColor = background ?: MaterialTheme.colorScheme.background.toArgb()
            ) { scope.launch { SettingsStore.setBackground(ctx, it) } }

            ColorPickerRow(
                label = "On Background",
                currentColor = onBackground ?: MaterialTheme.colorScheme.onBackground.toArgb()
            ) { scope.launch { SettingsStore.setOnBackground(ctx, it) } }

            // === Surface ===
            ColorPickerRow(
                label = "Surface",
                currentColor = surface ?: MaterialTheme.colorScheme.surface.toArgb()
            ) { scope.launch { SettingsStore.setSurface(ctx, it) } }

            ColorPickerRow(
                label = "On Surface",
                currentColor = onSurface ?: MaterialTheme.colorScheme.onSurface.toArgb()
            ) { scope.launch { SettingsStore.setOnSurface(ctx, it) } }

            // === Error ===
            ColorPickerRow(
                label = "Error",
                currentColor = error ?: MaterialTheme.colorScheme.error.toArgb()
            ) { scope.launch { SettingsStore.setError(ctx, it) } }

            ColorPickerRow(
                label = "On Error",
                currentColor = onError ?: MaterialTheme.colorScheme.onError.toArgb()
            ) { scope.launch { SettingsStore.setOnError(ctx, it) } }

            // === Outline ===
            ColorPickerRow(
                label = "Outline",
                currentColor = outline ?: MaterialTheme.colorScheme.outline.toArgb()
            ) { scope.launch { SettingsStore.setOutline(ctx, it) } }

            // === Extra custom action colors ===
            ColorPickerRow(
                label = "Delete",
                currentColor = delete ?: MaterialTheme.colorScheme.error.toArgb()
            ) { scope.launch { SettingsStore.setDelete(ctx, it) } }

            ColorPickerRow(
                label = "Edit",
                currentColor = edit ?: MaterialTheme.colorScheme.secondary.toArgb()
            ) { scope.launch { SettingsStore.setEdit(ctx, it) } }

            ColorPickerRow(
                label = "Complete",
                currentColor = complete ?: MaterialTheme.colorScheme.primary.toArgb()
            ) { scope.launch { SettingsStore.setComplete(ctx, it) } }

            // === Reset button ===
            Button(
                onClick = { scope.launch { SettingsStore.resetColors(ctx) } },
                modifier = Modifier.fillMaxWidth(),
                colors = AppObjectsColors.buttonColors()
            ) {
                Text("Reset to Default Colors")
            }
        }
    }
}
