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
import org.elnix.notes.ui.theme.BackgroundDefault
import org.elnix.notes.ui.theme.CompleteDefault
import org.elnix.notes.ui.theme.DeleteDefault
import org.elnix.notes.ui.theme.EditDefault
import org.elnix.notes.ui.theme.ErrorDefault
import org.elnix.notes.ui.theme.LocalExtraColors
import org.elnix.notes.ui.theme.OnBackgroundDefault
import org.elnix.notes.ui.theme.OnErrorDefault
import org.elnix.notes.ui.theme.OnPrimaryDefault
import org.elnix.notes.ui.theme.OnSecondaryDefault
import org.elnix.notes.ui.theme.OnSurfaceDefault
import org.elnix.notes.ui.theme.OnTertiaryDefault
import org.elnix.notes.ui.theme.OutlineDefault
import org.elnix.notes.ui.theme.PrimaryDefault
import org.elnix.notes.ui.theme.SecondaryDefault
import org.elnix.notes.ui.theme.SurfaceDefault
import org.elnix.notes.ui.theme.TertiaryDefault

@Composable
fun ColorSelectorTab(
    ctx: Context,
    scope: CoroutineScope,
    onBack: (() -> Unit)
) {

    // === Collect all theme color states ===
    val primary by ColorSettingsStore.getPrimary(ctx).collectAsState(initial = null)
    val onPrimary by ColorSettingsStore.getOnPrimary(ctx).collectAsState(initial = null)

    val secondary by ColorSettingsStore.getSecondary(ctx).collectAsState(initial = null)
    val onSecondary by ColorSettingsStore.getOnSecondary(ctx).collectAsState(initial = null)

    val tertiary by ColorSettingsStore.getTertiary(ctx).collectAsState(initial = null)
    val onTertiary by ColorSettingsStore.getOnTertiary(ctx).collectAsState(initial = null)

    val background by ColorSettingsStore.getBackground(ctx).collectAsState(initial = null)
    val onBackground by ColorSettingsStore.getOnBackground(ctx).collectAsState(initial = null)

    val surface by ColorSettingsStore.getSurface(ctx).collectAsState(initial = null)
    val onSurface by ColorSettingsStore.getOnSurface(ctx).collectAsState(initial = null)

    val error by ColorSettingsStore.getError(ctx).collectAsState(initial = null)
    val onError by ColorSettingsStore.getOnError(ctx).collectAsState(initial = null)

    val outline by ColorSettingsStore.getOutline(ctx).collectAsState(initial = null)

    val delete by ColorSettingsStore.getDelete(ctx).collectAsState(initial = null)
    val edit by ColorSettingsStore.getEdit(ctx).collectAsState(initial = null)
    val complete by ColorSettingsStore.getComplete(ctx).collectAsState(initial = null)

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
                defaultColor = PrimaryDefault,
                currentColor = primary ?: MaterialTheme.colorScheme.primary.toArgb(),
                scope = scope,
            ) { scope.launch { ColorSettingsStore.setPrimary(ctx, it) } }

            ColorPickerRow(
                label = "On Primary",
                defaultColor = OnPrimaryDefault,
                currentColor = onPrimary ?: MaterialTheme.colorScheme.onPrimary.toArgb(),
                scope = scope,
            ) { scope.launch { ColorSettingsStore.setOnPrimary(ctx, it) } }

            // === Secondary set ===
            ColorPickerRow(
                label = "Secondary",
                defaultColor = SecondaryDefault,
                currentColor = secondary ?: MaterialTheme.colorScheme.secondary.toArgb(),
                scope = scope,
            ) { scope.launch { ColorSettingsStore.setSecondary(ctx, it) } }

            ColorPickerRow(
                label = "On Secondary",
                defaultColor = OnSecondaryDefault,
                currentColor = onSecondary ?: MaterialTheme.colorScheme.onSecondary.toArgb(),
                scope = scope,
            ) { scope.launch { ColorSettingsStore.setOnSecondary(ctx, it) } }

            // === Tertiary set ===
            ColorPickerRow(
                label = "Tertiary",
                defaultColor = TertiaryDefault,
                currentColor = tertiary ?: MaterialTheme.colorScheme.tertiary.toArgb(),
                scope = scope,
            ) { scope.launch { ColorSettingsStore.setTertiary(ctx, it) } }

            ColorPickerRow(
                label = "On Tertiary",
                defaultColor = OnTertiaryDefault,
                currentColor = onTertiary ?: MaterialTheme.colorScheme.onTertiary.toArgb(),
                scope = scope,
            ) { scope.launch { ColorSettingsStore.setOnTertiary(ctx, it) } }

            // === Background ===
            ColorPickerRow(
                label = "Background",
                defaultColor = BackgroundDefault,
                currentColor = background ?: MaterialTheme.colorScheme.background.toArgb(),
                scope = scope,
            ) { scope.launch { ColorSettingsStore.setBackground(ctx, it) } }

            ColorPickerRow(
                label = "On Background",
                defaultColor = OnBackgroundDefault,
                currentColor = onBackground ?: MaterialTheme.colorScheme.onBackground.toArgb(),
                scope = scope,
            ) { scope.launch { ColorSettingsStore.setOnBackground(ctx, it) } }

            // === Surface ===
            ColorPickerRow(
                label = "Surface",
                defaultColor = SurfaceDefault,
                currentColor = surface ?: MaterialTheme.colorScheme.surface.toArgb(),
                scope = scope,
            ) { scope.launch { ColorSettingsStore.setSurface(ctx, it) } }

            ColorPickerRow(
                label = "On Surface",
                defaultColor = OnSurfaceDefault,
                currentColor = onSurface ?: MaterialTheme.colorScheme.onSurface.toArgb(),
                scope = scope,
            ) { scope.launch { ColorSettingsStore.setOnSurface(ctx, it) } }

            // === Error ===
            ColorPickerRow(
                label = "Error",
                defaultColor = ErrorDefault,
                currentColor = error ?: MaterialTheme.colorScheme.error.toArgb(),
                scope = scope,
            ) { scope.launch { ColorSettingsStore.setError(ctx, it) } }

            ColorPickerRow(
                label = "On Error",
                defaultColor = OnErrorDefault,
                currentColor = onError ?: MaterialTheme.colorScheme.onError.toArgb(),
                scope = scope,
            ) { scope.launch { ColorSettingsStore.setOnError(ctx, it) } }

            // === Outline ===
            ColorPickerRow(
                label = "Outline",
                defaultColor = OutlineDefault,
                currentColor = outline ?: MaterialTheme.colorScheme.outline.toArgb(),
                scope = scope,
            ) { scope.launch { ColorSettingsStore.setOutline(ctx, it) } }

            // === Extra custom action colors ===
            ColorPickerRow(
                label = "Delete",
                defaultColor = DeleteDefault,
                currentColor = delete ?: LocalExtraColors.current.delete.toArgb(),
                scope = scope,
            ) { scope.launch { ColorSettingsStore.setDelete(ctx, it) } }

            ColorPickerRow(
                label = "Edit",
                defaultColor = EditDefault,
                currentColor = edit ?: LocalExtraColors.current.edit.toArgb(),
                scope = scope,
            ) { scope.launch { ColorSettingsStore.setEdit(ctx, it) } }

            ColorPickerRow(
                label = "Complete",
                defaultColor = CompleteDefault,
                currentColor = complete ?: LocalExtraColors.current.complete.toArgb(),
                scope = scope,
            ) { scope.launch { ColorSettingsStore.setComplete(ctx, it) } }

            // === Reset button ===
            Button(
                onClick = { scope.launch { ColorSettingsStore.resetColors(ctx) } },
                modifier = Modifier.fillMaxWidth(),
                colors = AppObjectsColors.buttonColors()
            ) {
                Text(
                    text = "Reset to Default Colors",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}
