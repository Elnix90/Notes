package org.elnix.notes.ui.settings.appearance

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.elnix.notes.R
import org.elnix.notes.data.settings.ColorCustomisationMode
import org.elnix.notes.data.settings.DefaultThemes
import org.elnix.notes.data.settings.applyDefaultThemeColors
import org.elnix.notes.data.settings.stores.ColorSettingsStore
import org.elnix.notes.data.settings.stores.UiSettingsStore
import org.elnix.notes.ui.helpers.ActionSelectorRow
import org.elnix.notes.ui.helpers.SettingsTitle
import org.elnix.notes.ui.helpers.UserValidation
import org.elnix.notes.ui.helpers.colors.ColorPickerRow
import org.elnix.notes.ui.theme.AmoledDefault
import org.elnix.notes.ui.theme.AppObjectsColors
import org.elnix.notes.ui.theme.LocalExtraColors
import org.elnix.notes.ui.theme.adjustBrightness
import org.elnix.notes.ui.theme.blendWith

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

    val noteTypeText by ColorSettingsStore.getNoteTypeText(ctx).collectAsState(initial = null)
    val noteTypeChecklist by ColorSettingsStore.getNoteTypeChecklist(ctx).collectAsState(initial = null)
    val noteTypeDrawing by ColorSettingsStore.getNoteTypeDrawing(ctx).collectAsState(initial = null)


    val colorCustomisationMode by UiSettingsStore.getColorCustomisationMode(ctx).collectAsState(initial = ColorCustomisationMode.DEFAULT)
    val selectedDefaultTheme by UiSettingsStore.getDefaultTheme(ctx).collectAsState(initial = DefaultThemes.DARK)

    var showResetValidation by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    // === Layout ===
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SettingsTitle(title = stringResource(R.string.color_selector), onBack = onBack)

        ActionSelectorRow(
            options = ColorCustomisationMode.entries,
            selected = colorCustomisationMode,
            label = stringResource(R.string.color_custom_mode)
        ) {
            scope.launch { UiSettingsStore.setColorCustomisationMode(ctx, it) }
        }

        Button(
            onClick = { showResetValidation = true },
            modifier = Modifier.fillMaxWidth(),
            colors = AppObjectsColors.buttonColors()
        ) {
            Text(
                text = stringResource(R.string.reset_to_default_colors),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline)

        when (colorCustomisationMode) {
            ColorCustomisationMode.ALL -> {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                    ColorPickerRow(
                        label = stringResource(R.string.primary_color),
                        defaultColor = AmoledDefault.Primary,
                        currentColor = primary ?: MaterialTheme.colorScheme.primary.toArgb(),
                        scope = scope,
                    ) { scope.launch { ColorSettingsStore.setPrimary(ctx, it) } }

                    ColorPickerRow(
                        label = stringResource(R.string.on_primary_color),
                        defaultColor = AmoledDefault.OnPrimary,
                        currentColor = onPrimary ?: MaterialTheme.colorScheme.onPrimary.toArgb(),
                        scope = scope,
                    ) { scope.launch { ColorSettingsStore.setOnPrimary(ctx, it) } }

                    ColorPickerRow(
                        label = stringResource(R.string.secondary_color),
                        defaultColor = AmoledDefault.Secondary,
                        currentColor = secondary ?: MaterialTheme.colorScheme.secondary.toArgb(),
                        scope = scope,
                    ) { scope.launch { ColorSettingsStore.setSecondary(ctx, it) } }

                    ColorPickerRow(
                        label = stringResource(R.string.on_secondary_color),
                        defaultColor = AmoledDefault.OnSecondary,
                        currentColor = onSecondary
                            ?: MaterialTheme.colorScheme.onSecondary.toArgb(),
                        scope = scope,
                    ) { scope.launch { ColorSettingsStore.setOnSecondary(ctx, it) } }

                    ColorPickerRow(
                        label = stringResource(R.string.tertiary_color),
                        defaultColor = AmoledDefault.Tertiary,
                        currentColor = tertiary ?: MaterialTheme.colorScheme.tertiary.toArgb(),
                        scope = scope,
                    ) { scope.launch { ColorSettingsStore.setTertiary(ctx, it) } }

                    ColorPickerRow(
                        label = stringResource(R.string.on_tertiary_color),
                        defaultColor = AmoledDefault.OnTertiary,
                        currentColor = onTertiary ?: MaterialTheme.colorScheme.onTertiary.toArgb(),
                        scope = scope,
                    ) { scope.launch { ColorSettingsStore.setOnTertiary(ctx, it) } }

                    ColorPickerRow(
                        label = stringResource(R.string.background_color),
                        defaultColor = AmoledDefault.Background,
                        currentColor = background ?: MaterialTheme.colorScheme.background.toArgb(),
                        scope = scope,
                    ) { scope.launch { ColorSettingsStore.setBackground(ctx, it) } }

                    ColorPickerRow(
                        label = stringResource(R.string.on_background_color),
                        defaultColor = AmoledDefault.OnBackground,
                        currentColor = onBackground
                            ?: MaterialTheme.colorScheme.onBackground.toArgb(),
                        scope = scope,
                    ) { scope.launch { ColorSettingsStore.setOnBackground(ctx, it) } }

                    ColorPickerRow(
                        label = stringResource(R.string.surface_color),
                        defaultColor = AmoledDefault.Surface,
                        currentColor = surface ?: MaterialTheme.colorScheme.surface.toArgb(),
                        scope = scope,
                    ) { scope.launch { ColorSettingsStore.setSurface(ctx, it) } }

                    ColorPickerRow(
                        label = stringResource(R.string.on_surface_color),
                        defaultColor = AmoledDefault.OnSurface,
                        currentColor = onSurface ?: MaterialTheme.colorScheme.onSurface.toArgb(),
                        scope = scope,
                    ) { scope.launch { ColorSettingsStore.setOnSurface(ctx, it) } }

                    ColorPickerRow(
                        label = stringResource(R.string.error_color),
                        defaultColor = AmoledDefault.Error,
                        currentColor = error ?: MaterialTheme.colorScheme.error.toArgb(),
                        scope = scope,
                    ) { scope.launch { ColorSettingsStore.setError(ctx, it) } }

                    ColorPickerRow(
                        label = stringResource(R.string.on_error_color),
                        defaultColor = AmoledDefault.OnError,
                        currentColor = onError ?: MaterialTheme.colorScheme.onError.toArgb(),
                        scope = scope,
                    ) { scope.launch { ColorSettingsStore.setOnError(ctx, it) } }

                    ColorPickerRow(
                        label = stringResource(R.string.outline_color),
                        defaultColor = AmoledDefault.Outline,
                        currentColor = outline ?: MaterialTheme.colorScheme.outline.toArgb(),
                        scope = scope,
                    ) { scope.launch { ColorSettingsStore.setOutline(ctx, it) } }

                    // === Extra custom action colors ===
                    ColorPickerRow(
                        label = stringResource(R.string.delete_color),
                        defaultColor = AmoledDefault.Delete,
                        currentColor = delete ?: LocalExtraColors.current.delete.toArgb(),
                        scope = scope,
                    ) { scope.launch { ColorSettingsStore.setDelete(ctx, it) } }

                    ColorPickerRow(
                        label = stringResource(R.string.edit_color),
                        defaultColor = AmoledDefault.Edit,
                        currentColor = edit ?: LocalExtraColors.current.edit.toArgb(),
                        scope = scope,
                    ) { scope.launch { ColorSettingsStore.setEdit(ctx, it) } }

                    ColorPickerRow(
                        label = stringResource(R.string.complete_color),
                        defaultColor = AmoledDefault.Complete,
                        currentColor = complete ?: LocalExtraColors.current.complete.toArgb(),
                        scope = scope,
                    ) { scope.launch { ColorSettingsStore.setComplete(ctx, it) } }

                    ColorPickerRow(
                        label = stringResource(R.string.note_type_text),
                        defaultColor = AmoledDefault.NoteTypeText,
                        currentColor = noteTypeText ?: LocalExtraColors.current.noteTypeText.toArgb(),
                        scope = scope,
                    ) { scope.launch { ColorSettingsStore.setNoteTypeText(ctx, it) } }

                    ColorPickerRow(
                        label = stringResource(R.string.note_type_checklist),
                        defaultColor = AmoledDefault.NoteTypeChecklist,
                        currentColor = noteTypeChecklist ?: LocalExtraColors.current.noteTypeChecklist.toArgb(),
                        scope = scope,
                    ) { scope.launch { ColorSettingsStore.setNoteTypeChecklist(ctx, it) } }

                    ColorPickerRow(
                        label = stringResource(R.string.note_type_drawing),
                        defaultColor = AmoledDefault.NoteTypeDrawing,
                        currentColor = noteTypeDrawing ?: LocalExtraColors.current.noteTypeDrawing.toArgb(),
                        scope = scope,
                    ) { scope.launch { ColorSettingsStore.setNoteTypeDrawing(ctx, it) } }

                }
            }

            ColorCustomisationMode.NORMAL -> {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    val bgColorFromTheme = MaterialTheme.colorScheme.background
                    ColorPickerRow(
                        label = stringResource(R.string.primary_color),
                        defaultColor = AmoledDefault.Primary,
                        currentColor = primary ?: MaterialTheme.colorScheme.primary.toArgb(),
                        scope = scope,
                    ) { newColorInt ->


                        val currentColorObject = Color(newColorInt)

                        val backgroundColor = background?.let { Color(it) } ?: bgColorFromTheme

                        val secondaryColor = currentColorObject.adjustBrightness(1.2f)
                        val tertiaryColor = secondaryColor.adjustBrightness(1.2f)
                        val surfaceColor = currentColorObject.blendWith(backgroundColor, 0.7f)

                        scope.launch {
                            ColorSettingsStore.setPrimary(ctx, newColorInt)
                            ColorSettingsStore.setSecondary(ctx, secondaryColor.toArgb())
                            ColorSettingsStore.setTertiary(ctx, tertiaryColor.toArgb())
                            ColorSettingsStore.setSurface(ctx, surfaceColor.toArgb())
                        }
                    }

                    ColorPickerRow(
                        label = stringResource(R.string.background_color),
                        defaultColor = AmoledDefault.Background,
                        currentColor = background ?: MaterialTheme.colorScheme.background.toArgb(),
                        scope = scope,
                    ) {
                        scope.launch {
                            ColorSettingsStore.setBackground(ctx, it)
                        }
                    }

                    ColorPickerRow(
                        label = stringResource(R.string.text_color),
                        defaultColor = AmoledDefault.OnPrimary,
                        currentColor = onPrimary ?: MaterialTheme.colorScheme.onPrimary.toArgb(),
                        scope = scope,
                    ) {
                        scope.launch {
                            ColorSettingsStore.setOnPrimary(ctx, it)
                            ColorSettingsStore.setOnSecondary(ctx, it)
                            ColorSettingsStore.setOnTertiary(ctx, it)
                            ColorSettingsStore.setOnSurface(ctx, it)
                            ColorSettingsStore.setOnBackground(ctx, it)
                            ColorSettingsStore.setOutline(ctx, it)
                            ColorSettingsStore.setOnError(ctx, it)
                        }
                    }
                }
            }

            ColorCustomisationMode.DEFAULT ->  {
                ActionSelectorRow(
                    options = DefaultThemes.entries,
                    selected = selectedDefaultTheme,
                    label = stringResource(R.string.theme)
                ) {
                    scope.launch {
                        UiSettingsStore.setDefaultTheme(ctx, it)
                        applyDefaultThemeColors(ctx, it)
                    }
                }
            }
        }
    }

    if(showResetValidation){
        UserValidation(
            title = stringResource(R.string.reset_to_default_colors),
            message = stringResource(R.string.reset_to_default_colors_explanation),
            onCancel = { showResetValidation = false }
        ) {
            scope.launch {
                ColorSettingsStore.resetColors(
                    ctx,
                    colorCustomisationMode,
                    selectedDefaultTheme
                )
                showResetValidation = false
            }
        }
    }
}
