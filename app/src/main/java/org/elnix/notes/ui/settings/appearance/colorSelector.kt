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
import org.elnix.notes.data.settings.ColorSettingsStore
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
    val primary by ColorSettingsStore.getPrimaryFlow(ctx).collectAsState(initial = null)
    val onPrimary by ColorSettingsStore.getOnPrimaryFlow(ctx).collectAsState(initial = null)

    val secondary by ColorSettingsStore.getSecondaryFlow(ctx).collectAsState(initial = null)
    val onSecondary by ColorSettingsStore.getOnSecondaryFlow(ctx).collectAsState(initial = null)

    val tertiary by ColorSettingsStore.getTertiaryFlow(ctx).collectAsState(initial = null)
    val onTertiary by ColorSettingsStore.getOnTertiaryFlow(ctx).collectAsState(initial = null)

    val background by ColorSettingsStore.getBackgroundFlow(ctx).collectAsState(initial = null)
    val onBackground by ColorSettingsStore.getOnBackgroundFlow(ctx).collectAsState(initial = null)

    val surface by ColorSettingsStore.getSurfaceFlow(ctx).collectAsState(initial = null)
    val onSurface by ColorSettingsStore.getOnSurfaceFlow(ctx).collectAsState(initial = null)

    val error by ColorSettingsStore.getErrorFlow(ctx).collectAsState(initial = null)
    val onError by ColorSettingsStore.getOnErrorFlow(ctx).collectAsState(initial = null)

    val outline by ColorSettingsStore.getOutlineFlow(ctx).collectAsState(initial = null)

    val delete by ColorSettingsStore.getDeleteFlow(ctx).collectAsState(initial = null)
    val edit by ColorSettingsStore.getEditFlow(ctx).collectAsState(initial = null)
    val complete by ColorSettingsStore.getCompleteFlow(ctx).collectAsState(initial = null)

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
            ) { scope.launch { ColorSettingsStore.setPrimary(ctx, it) } }

            ColorPickerRow(
                label = "On Primary",
                currentColor = onPrimary ?: MaterialTheme.colorScheme.onPrimary.toArgb()
            ) { scope.launch { ColorSettingsStore.setOnPrimary(ctx, it) } }

            // === Secondary set ===
            ColorPickerRow(
                label = "Secondary",
                currentColor = secondary ?: MaterialTheme.colorScheme.secondary.toArgb()
            ) { scope.launch { ColorSettingsStore.setSecondary(ctx, it) } }

            ColorPickerRow(
                label = "On Secondary",
                currentColor = onSecondary ?: MaterialTheme.colorScheme.onSecondary.toArgb()
            ) { scope.launch { ColorSettingsStore.setOnSecondary(ctx, it) } }

            // === Tertiary set ===
            ColorPickerRow(
                label = "Tertiary",
                currentColor = tertiary ?: MaterialTheme.colorScheme.tertiary.toArgb()
            ) { scope.launch { ColorSettingsStore.setTertiary(ctx, it) } }

            ColorPickerRow(
                label = "On Tertiary",
                currentColor = onTertiary ?: MaterialTheme.colorScheme.onTertiary.toArgb()
            ) { scope.launch { ColorSettingsStore.setOnTertiary(ctx, it) } }

            // === Background ===
            ColorPickerRow(
                label = "Background",
                currentColor = background ?: MaterialTheme.colorScheme.background.toArgb()
            ) { scope.launch { ColorSettingsStore.setBackground(ctx, it) } }

            ColorPickerRow(
                label = "On Background",
                currentColor = onBackground ?: MaterialTheme.colorScheme.onBackground.toArgb()
            ) { scope.launch { ColorSettingsStore.setOnBackground(ctx, it) } }

            // === Surface ===
            ColorPickerRow(
                label = "Surface",
                currentColor = surface ?: MaterialTheme.colorScheme.surface.toArgb()
            ) { scope.launch { ColorSettingsStore.setSurface(ctx, it) } }

            ColorPickerRow(
                label = "On Surface",
                currentColor = onSurface ?: MaterialTheme.colorScheme.onSurface.toArgb()
            ) { scope.launch { ColorSettingsStore.setOnSurface(ctx, it) } }

            // === Error ===
            ColorPickerRow(
                label = "Error",
                currentColor = error ?: MaterialTheme.colorScheme.error.toArgb()
            ) { scope.launch { ColorSettingsStore.setError(ctx, it) } }

            ColorPickerRow(
                label = "On Error",
                currentColor = onError ?: MaterialTheme.colorScheme.onError.toArgb()
            ) { scope.launch { ColorSettingsStore.setOnError(ctx, it) } }

            // === Outline ===
            ColorPickerRow(
                label = "Outline",
                currentColor = outline ?: MaterialTheme.colorScheme.outline.toArgb()
            ) { scope.launch { ColorSettingsStore.setOutline(ctx, it) } }

            // === Extra custom action colors ===
            ColorPickerRow(
                label = "Delete",
                currentColor = delete ?: MaterialTheme.colorScheme.error.toArgb()
            ) { scope.launch { ColorSettingsStore.setDelete(ctx, it) } }

            ColorPickerRow(
                label = "Edit",
                currentColor = edit ?: MaterialTheme.colorScheme.secondary.toArgb()
            ) { scope.launch { ColorSettingsStore.setEdit(ctx, it) } }

            ColorPickerRow(
                label = "Complete",
                currentColor = complete ?: MaterialTheme.colorScheme.primary.toArgb()
            ) { scope.launch { ColorSettingsStore.setComplete(ctx, it) } }

            // === Reset button ===
            Button(
                onClick = { scope.launch { ColorSettingsStore.resetColors(ctx) } },
                modifier = Modifier.fillMaxWidth(),
                colors = AppObjectsColors.buttonColors()
            ) {
                Text("Reset to Default Colors")
            }
        }
    }
}
