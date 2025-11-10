package org.elnix.notes.ui.settings.appearance

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import org.elnix.notes.ui.helpers.UserValidation
import org.elnix.notes.ui.helpers.colors.ColorPickerRow
import org.elnix.notes.ui.helpers.settings.SettingsTitle
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
    var showRandomColorsValidation by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(WindowInsets.systemBars.asPaddingValues())
            .padding(horizontal = 16.dp)
            .imePadding()
    ) {

        Surface(color = MaterialTheme.colorScheme.background, tonalElevation = 3.dp) {
            SettingsTitle(title = stringResource(R.string.color_selector)) { onBack() }
            Spacer(Modifier.height(20.dp))
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(bottom = 400.dp)
        ) {

            item {
                ActionSelectorRow(
                    options = ColorCustomisationMode.entries,
                    selected = colorCustomisationMode,
                    label = stringResource(R.string.color_custom_mode)
                ) {
                    scope.launch { UiSettingsStore.setColorCustomisationMode(ctx, it) }
                }
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { showResetValidation = true },
                        modifier = Modifier.weight(1f),
                        colors = AppObjectsColors.buttonColors()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Restore,
                            contentDescription = stringResource(R.string.reset),
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier
                                .clip(CircleShape)
                                .padding(5.dp)
                        )

                        Text(
                            text = stringResource(R.string.reset_to_default_colors),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    IconButton(
                        onClick = { showRandomColorsValidation = true },
                        colors = AppObjectsColors.iconButtonColors(
                            backgroundColor = MaterialTheme.colorScheme.primary.copy(0.7f)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shuffle,
                            contentDescription = stringResource(R.string.make_every_colors_random),
                            modifier = Modifier
                                .clip(CircleShape)
                                .padding(5.dp)
                        )
                    }
                }
            }

            item { HorizontalDivider(color = MaterialTheme.colorScheme.outline) }

            when (colorCustomisationMode) {
                ColorCustomisationMode.ALL -> {

                    item {
                        ColorPickerRow(
                            label = stringResource(R.string.primary_color),
                            defaultColor = AmoledDefault.Primary,
                            currentColor = primary ?: MaterialTheme.colorScheme.primary.toArgb()
                        ) { scope.launch { ColorSettingsStore.setPrimary(ctx, it) } }
                    }

                    item {
                        ColorPickerRow(
                            label = stringResource(R.string.on_primary_color),
                            defaultColor = AmoledDefault.OnPrimary,
                            currentColor = onPrimary ?: MaterialTheme.colorScheme.onPrimary.toArgb()
                        ) { scope.launch { ColorSettingsStore.setOnPrimary(ctx, it) } }
                    }

                    item {
                        ColorPickerRow(
                            label = stringResource(R.string.secondary_color),
                            defaultColor = AmoledDefault.Secondary,
                            currentColor = secondary ?: MaterialTheme.colorScheme.secondary.toArgb()
                        ) { scope.launch { ColorSettingsStore.setSecondary(ctx, it) } }
                    }

                    item {
                        ColorPickerRow(
                            label = stringResource(R.string.on_secondary_color),
                            defaultColor = AmoledDefault.OnSecondary,
                            currentColor = onSecondary
                                ?: MaterialTheme.colorScheme.onSecondary.toArgb()
                        ) { scope.launch { ColorSettingsStore.setOnSecondary(ctx, it) } }
                    }

                    item {
                        ColorPickerRow(
                            label = stringResource(R.string.tertiary_color),
                            defaultColor = AmoledDefault.Tertiary,
                            currentColor = tertiary ?: MaterialTheme.colorScheme.tertiary.toArgb()
                        ) { scope.launch { ColorSettingsStore.setTertiary(ctx, it) } }
                    }

                    item {
                        ColorPickerRow(
                            label = stringResource(R.string.on_tertiary_color),
                            defaultColor = AmoledDefault.OnTertiary,
                            currentColor = onTertiary
                                ?: MaterialTheme.colorScheme.onTertiary.toArgb()
                        ) { scope.launch { ColorSettingsStore.setOnTertiary(ctx, it) } }
                    }

                    item {
                        ColorPickerRow(
                            label = stringResource(R.string.background_color),
                            defaultColor = AmoledDefault.Background,
                            currentColor = background
                                ?: MaterialTheme.colorScheme.background.toArgb()
                        ) { scope.launch { ColorSettingsStore.setBackground(ctx, it) } }
                    }

                    item {
                        ColorPickerRow(
                            label = stringResource(R.string.on_background_color),
                            defaultColor = AmoledDefault.OnBackground,
                            currentColor = onBackground
                                ?: MaterialTheme.colorScheme.onBackground.toArgb()
                        ) { scope.launch { ColorSettingsStore.setOnBackground(ctx, it) } }
                    }

                    item {
                        ColorPickerRow(
                            label = stringResource(R.string.surface_color),
                            defaultColor = AmoledDefault.Surface,
                            currentColor = surface ?: MaterialTheme.colorScheme.surface.toArgb()
                        ) { scope.launch { ColorSettingsStore.setSurface(ctx, it) } }
                    }

                    item {
                        ColorPickerRow(
                            label = stringResource(R.string.on_surface_color),
                            defaultColor = AmoledDefault.OnSurface,
                            currentColor = onSurface ?: MaterialTheme.colorScheme.onSurface.toArgb()
                        ) { scope.launch { ColorSettingsStore.setOnSurface(ctx, it) } }
                    }

                    item {
                        ColorPickerRow(
                            label = stringResource(R.string.error_color),
                            defaultColor = AmoledDefault.Error,
                            currentColor = error ?: MaterialTheme.colorScheme.error.toArgb()
                        ) { scope.launch { ColorSettingsStore.setError(ctx, it) } }
                    }

                    item {
                        ColorPickerRow(
                            label = stringResource(R.string.on_error_color),
                            defaultColor = AmoledDefault.OnError,
                            currentColor = onError ?: MaterialTheme.colorScheme.onError.toArgb()
                        ) { scope.launch { ColorSettingsStore.setOnError(ctx, it) } }
                    }

                    item {
                        ColorPickerRow(
                            label = stringResource(R.string.outline_color),
                            defaultColor = AmoledDefault.Outline,
                            currentColor = outline ?: MaterialTheme.colorScheme.outline.toArgb()
                        ) { scope.launch { ColorSettingsStore.setOutline(ctx, it) } }
                    }

                    // === Extra custom action colors ===
                    item {
                        ColorPickerRow(
                            label = stringResource(R.string.delete_color),
                            defaultColor = AmoledDefault.Delete,
                            currentColor = delete ?: LocalExtraColors.current.delete.toArgb()
                        ) { scope.launch { ColorSettingsStore.setDelete(ctx, it) } }
                    }

                    item {
                        ColorPickerRow(
                            label = stringResource(R.string.edit_color),
                            defaultColor = AmoledDefault.Edit,
                            currentColor = edit ?: LocalExtraColors.current.edit.toArgb()
                        ) { scope.launch { ColorSettingsStore.setEdit(ctx, it) } }
                    }

                    item {
                        ColorPickerRow(
                            label = stringResource(R.string.complete_color),
                            defaultColor = AmoledDefault.Complete,
                            currentColor = complete ?: LocalExtraColors.current.complete.toArgb()
                        ) { scope.launch { ColorSettingsStore.setComplete(ctx, it) } }
                    }

                    item {
                        ColorPickerRow(
                            label = stringResource(R.string.note_type_text),
                            defaultColor = AmoledDefault.NoteTypeText,
                            currentColor = noteTypeText
                                ?: LocalExtraColors.current.noteTypeText.toArgb()
                        ) { scope.launch { ColorSettingsStore.setNoteTypeText(ctx, it) } }
                    }

                    item {
                        ColorPickerRow(
                            label = stringResource(R.string.note_type_checklist),
                            defaultColor = AmoledDefault.NoteTypeChecklist,
                            currentColor = noteTypeChecklist
                                ?: LocalExtraColors.current.noteTypeChecklist.toArgb()
                        ) { scope.launch { ColorSettingsStore.setNoteTypeChecklist(ctx, it) } }
                    }

                    item {
                        ColorPickerRow(
                            label = stringResource(R.string.note_type_drawing),
                            defaultColor = AmoledDefault.NoteTypeDrawing,
                            currentColor = noteTypeDrawing
                                ?: LocalExtraColors.current.noteTypeDrawing.toArgb()
                        ) { scope.launch { ColorSettingsStore.setNoteTypeDrawing(ctx, it) } }
                    }
                }

                ColorCustomisationMode.NORMAL -> {
                    item {
                        val bgColorFromTheme = MaterialTheme.colorScheme.background
                        ColorPickerRow(
                            label = stringResource(R.string.primary_color),
                            defaultColor = AmoledDefault.Primary,
                            currentColor = primary ?: MaterialTheme.colorScheme.primary.toArgb()
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
                    }

                    item {
                        ColorPickerRow(
                            label = stringResource(R.string.background_color),
                            defaultColor = AmoledDefault.Background,
                            currentColor = background
                                ?: MaterialTheme.colorScheme.background.toArgb()
                        ) {
                            scope.launch {
                                ColorSettingsStore.setBackground(ctx, it)
                            }
                        }
                    }

                    item {
                        ColorPickerRow(
                            label = stringResource(R.string.text_color),
                            defaultColor = AmoledDefault.OnPrimary,
                            currentColor = onPrimary ?: MaterialTheme.colorScheme.onPrimary.toArgb()
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

                ColorCustomisationMode.DEFAULT -> {
                    item {
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
    if(showRandomColorsValidation){
        UserValidation(
            title = stringResource(R.string.make_every_colors_random),
            message = stringResource(R.string.make_every_colors_random_explanation),
            onCancel = { showRandomColorsValidation = false }
        ) {
            scope.launch {
                ColorSettingsStore.setAllRandomColors(ctx)
                showRandomColorsValidation = false
            }
        }
    }
}
