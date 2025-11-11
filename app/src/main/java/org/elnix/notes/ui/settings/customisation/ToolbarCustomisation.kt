package org.elnix.notes.ui.settings.customisation

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.elnix.notes.R
import org.elnix.notes.data.helpers.ToolBars
import org.elnix.notes.data.settings.stores.ToolbarItemsSettingsStore
import org.elnix.notes.data.settings.stores.ToolbarsSettingsStore
import org.elnix.notes.ui.helpers.TextDivider
import org.elnix.notes.ui.helpers.toolbars.SliderToolbarSetting
import org.elnix.notes.ui.helpers.toolbars.ToolbarColorSelectorDialog
import org.elnix.notes.ui.helpers.toolbars.ToolbarItemsEditor
import org.elnix.notes.ui.helpers.toolbars.UnifiedToolbar
import org.elnix.notes.ui.settings.SettingsLazyHeader
import org.elnix.notes.ui.theme.AppObjectsColors

@Composable
fun ToolbarCustomisationTab(
    ctx: Context,
    scope: CoroutineScope,
    toolbar: ToolBars,
    onBack: (() -> Unit)
) {
    val maxPadding = 100

    val toolbarsFlow = remember { ToolbarsSettingsStore.getToolbarsFlow(ctx) }
    val toolbars by toolbarsFlow.collectAsState(initial = emptyList())
    val toolbarSetting = toolbars.find { it.toolbar == toolbar }

    if (toolbarSetting == null) {
        return
    }

    var showColorPickerDialog by remember { mutableStateOf(false) }


    SettingsLazyHeader(
        title = stringResource(R.string.toolbar_customization),
        onBack = onBack,
        helpText = stringResource(R.string.toolbars_customisation_text),
        onReset = {
            scope.launch {
                ToolbarItemsSettingsStore.resetToolbar(ctx, toolbarSetting.toolbar)
            }
        }
    ) {

        item {
            UnifiedToolbar(
                ctx = ctx,
                toolbar = toolbarSetting.toolbar,
                scrollState = rememberScrollState(),
                isSearchExpanded = false,
                color = toolbarSetting.color,
                borderColor = toolbarSetting.borderColor,
                borderWidth = toolbarSetting.borderWidth,
                borderRadius = toolbarSetting.borderRadius,
                elevation = toolbarSetting.elevation,
                paddingLeft = toolbarSetting.leftPadding,
                paddingRight = toolbarSetting.rightPadding,
                ghosted = true
            )
        }


        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                Button(
                    onClick = { showColorPickerDialog = true },
                    colors = AppObjectsColors.buttonColors(),
                    shape = CircleShape,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.ColorLens,
                        contentDescription = stringResource(R.string.toolbar_color),
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(stringResource(R.string.toolbar_color))
                }

                Button(
                    onClick = {
                        scope.launch {
                            ToolbarsSettingsStore.resetToolbar(ctx, toolbarSetting.toolbar)
                        }
                    },
                    colors = AppObjectsColors.buttonColors(),
                    shape = CircleShape,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Restore,
                        contentDescription = stringResource(R.string.reset),
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(stringResource(R.string.reset))
                }
            }
        }

        // Border slider
        item {
            SliderToolbarSetting(
                label = { value ->
                    if (value > 0)
                        "${stringResource(R.string.toolbars_border)}:  $value px"
                    else stringResource(R.string.no_border)
                },
                initialValue = toolbarSetting.borderWidth,
                valueRange = 0f..20f,
                steps = 19,
                onReset = {
                    scope.launch {
                        ToolbarsSettingsStore.updateToolbarSetting(
                             ctx = ctx,
                             toolbar = toolbarSetting.toolbar
                        ) { it.copy(borderWidth = 2) }
                    }
                },
                onValueChangeFinished = { v ->
                    scope.launch {
                        ToolbarsSettingsStore.updateToolbarSetting(
                            ctx = ctx,
                            toolbar = toolbarSetting.toolbar
                        ) { it.copy(borderWidth = v) }
                    }
                }
            )
        }

        // Corner radius slider
        item {
            SliderToolbarSetting(
                label = { value ->
                    if (value > 0)
                        "${stringResource(R.string.corner_radius)}:  ${value * 2} %"
                    else stringResource(R.string.rectangle)
                },
                initialValue = toolbarSetting.borderRadius,
                valueRange = 0f..50f,
                steps = 49,
                onReset = {
                    scope.launch {
                        ToolbarsSettingsStore.updateToolbarSetting(
                            ctx = ctx,
                            toolbar = toolbarSetting.toolbar
                        ) { it.copy(borderRadius = 50) }
                    }
                },
                onValueChangeFinished = { v ->
                    scope.launch {
                        ToolbarsSettingsStore.updateToolbarSetting(
                            ctx = ctx,
                            toolbar = toolbarSetting.toolbar
                        ) { it.copy(borderRadius = v) }
                    }
                }
            )
        }

        // Padding left
        item {
            SliderToolbarSetting(
                label = { value ->
                    "${stringResource(R.string.padding)} ${stringResource(R.string.left)}: $value px"
                },
                initialValue = toolbarSetting.leftPadding,
                valueRange = 0f..maxPadding.toFloat(),
                steps = maxPadding - 1,
                onReset = {
                    scope.launch {
                        ToolbarsSettingsStore.updateToolbarSetting(
                            ctx = ctx,
                            toolbar = toolbarSetting.toolbar
                        ) { it.copy(leftPadding = 16) }
                    }
                },
                onValueChangeFinished = { v ->
                    scope.launch {
                        ToolbarsSettingsStore.updateToolbarSetting(
                            ctx = ctx,
                            toolbar = toolbarSetting.toolbar
                        ) { it.copy(leftPadding = v) }
                    }
                }
            )
        }

        item {
            // Padding right
            SliderToolbarSetting(
                label = { value ->
                    "${stringResource(R.string.padding)} ${stringResource(R.string.right)}: $value px"
                },
                initialValue = toolbarSetting.rightPadding,
                valueRange = 0f..maxPadding.toFloat(),
                steps = maxPadding - 1,
                onReset = {
                    scope.launch {
                        ToolbarsSettingsStore.updateToolbarSetting(
                            ctx = ctx,
                            toolbar = toolbarSetting.toolbar
                        ) { it.copy(rightPadding = 16) }
                    }
                },
                onValueChangeFinished = { v ->
                    scope.launch {
                        ToolbarsSettingsStore.updateToolbarSetting(
                            ctx = ctx,
                            toolbar = toolbarSetting.toolbar
                        ) { it.copy(rightPadding = v) }
                    }
                }
            )
        }

        item {
            // Toolbar Elevation
            SliderToolbarSetting(
                label = { value ->
                    "${stringResource(R.string.toolbar_evevation)}: $value px"
                },
                initialValue = toolbarSetting.elevation,
                valueRange = 0f..maxPadding.toFloat(),
                steps = maxPadding - 1,
                onReset = {
                    scope.launch {
                        ToolbarsSettingsStore.updateToolbarSetting(
                            ctx = ctx,
                            toolbar = toolbarSetting.toolbar
                        ) { it.copy(elevation = 3) }
                    }
                },
                onValueChangeFinished = { v ->
                    scope.launch {
                        ToolbarsSettingsStore.updateToolbarSetting(
                            ctx = ctx,
                            toolbar = toolbarSetting.toolbar
                        ) { it.copy(elevation = v) }
                    }
                }
            )
        }

        item {
            TextDivider(stringResource(R.string.toolbars_items_and_order))
        }

        item { ToolbarItemsEditor(ctx, toolbarSetting.toolbar) }
    }

    if (showColorPickerDialog) {
        ToolbarColorSelectorDialog(
            toolbar = toolbarSetting,
            onDismiss = { showColorPickerDialog = false }
        ) { color, borderColor ->
            scope.launch {
                ToolbarsSettingsStore.updateToolbarColor(
                    ctx = ctx,
                    toolbar = toolbarSetting.toolbar,
                    color = Color(color),
                    borderColor = Color(borderColor)
                )
            }
            showColorPickerDialog = false
        }
    }
}
