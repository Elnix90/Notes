package org.elnix.notes.ui.settings.customisation

import android.R.attr.spacing
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.elnix.notes.R
import org.elnix.notes.data.helpers.ToolBars
import org.elnix.notes.data.settings.stores.ToolbarsSettingsStore
import org.elnix.notes.ui.helpers.TextDivider
import org.elnix.notes.ui.helpers.toolbars.ToolbarItemsEditor
import org.elnix.notes.ui.helpers.toolbars.UnifiedToolbar
import org.elnix.notes.ui.settings.SettingsLazyHeader
import org.elnix.notes.ui.theme.AppObjectsColors
import kotlin.math.roundToInt

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
    val toolbar = toolbars.find { it.toolbar == toolbar }!!

    SettingsLazyHeader(
        title = stringResource(R.string.toolbar_customization),
        onBack = onBack
    ) {

        item {
            UnifiedToolbar(
                ctx = ctx,
                toolbar = toolbar.toolbar,
                scrollState = rememberScrollState(),
                isSearchExpanded = false,
                color = toolbar.color,
                borderColor = toolbar.borderColor,
                borderWidth = toolbar.borderWidth,
                borderRadius = toolbar.borderRadius,
                elevation = toolbar.elevation,
                paddingLeft = toolbar.leftPadding,
                paddingRight = toolbar.rightPadding,
                ghosted = true
            )
        }


        // Border slider
        item {
            SliderToolbarSetting(
                label = { value ->
                    if (value > 0)
                        "${stringResource(R.string.toolbars_border)}:  $value px"
                    else stringResource(R.string.no_border)
                },
                initialValue = toolbar.borderWidth,
                valueRange = 0f..20f,
                steps = 19,
                onReset = {
                    scope.launch {
                        ToolbarsSettingsStore.updateToolbarSetting(
                             ctx = ctx,
                             toolbar = toolbar.toolbar
                        ) { it.copy(borderWidth = 2) }
                    }
                },
                onValueChangeFinished = { v ->
                    scope.launch {
                        ToolbarsSettingsStore.updateToolbarSetting(
                            ctx = ctx,
                            toolbar = toolbar.toolbar
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
                initialValue = toolbar.borderRadius,
                valueRange = 0f..50f,
                steps = 49,
                onReset = {
                    scope.launch {
                        ToolbarsSettingsStore.updateToolbarSetting(
                            ctx = ctx,
                            toolbar = toolbar.toolbar
                        ) { it.copy(borderRadius = 50) }
                    }
                },
                onValueChangeFinished = { v ->
                    scope.launch {
                        ToolbarsSettingsStore.updateToolbarSetting(
                            ctx = ctx,
                            toolbar = toolbar.toolbar
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
                initialValue = toolbar.leftPadding,
                valueRange = 0f..maxPadding.toFloat(),
                steps = maxPadding - 1,
                onReset = {
                    scope.launch {
                        ToolbarsSettingsStore.updateToolbarSetting(
                            ctx = ctx,
                            toolbar = toolbar.toolbar
                        ) { it.copy(leftPadding = 16) }
                    }
                },
                onValueChangeFinished = { v ->
                    scope.launch {
                        ToolbarsSettingsStore.updateToolbarSetting(
                            ctx = ctx,
                            toolbar = toolbar.toolbar
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
                initialValue = toolbar.rightPadding,
                valueRange = 0f..maxPadding.toFloat(),
                steps = maxPadding - 1,
                onReset = {
                    scope.launch {
                        ToolbarsSettingsStore.updateToolbarSetting(
                            ctx = ctx,
                            toolbar = toolbar.toolbar
                        ) { it.copy(rightPadding = 16) }
                    }
                },
                onValueChangeFinished = { v ->
                    scope.launch {
                        ToolbarsSettingsStore.updateToolbarSetting(
                            ctx = ctx,
                            toolbar = toolbar.toolbar
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
                initialValue = toolbar.elevation,
                valueRange = 0f..maxPadding.toFloat(),
                steps = maxPadding - 1,
                onReset = {
                    scope.launch {
                        ToolbarsSettingsStore.updateToolbarSetting(
                            ctx = ctx,
                            toolbar = toolbar.toolbar
                        ) { it.copy(elevation = 3) }
                    }
                },
                onValueChangeFinished = { v ->
                    scope.launch {
                        ToolbarsSettingsStore.updateToolbarSetting(
                            ctx = ctx,
                            toolbar = toolbar.toolbar
                        ) { it.copy(elevation = v) }
                    }
                }
            )
        }

        // Toolbars spacing
        item {
            SliderToolbarSetting(
                label = { value ->
                    "${stringResource(R.string.toolbars_spacing)}: $value px"
                },
                initialValue = spacing,
                valueRange = 0f..maxPadding.toFloat(),
                steps = maxPadding - 1,
                onReset = { scope.launch { ToolbarsSettingsStore.setToolbarsSpacing(ctx, 8) } },
                onValueChangeFinished = { v ->
                    scope.launch { ToolbarsSettingsStore.setToolbarsSpacing(ctx, v) }
                }
            )
        }

        item {
            TextDivider(stringResource(R.string.toolbars_items_and_order))
        }

        item { ToolbarItemsEditor(ctx, toolbar.toolbar) }
    }
}


@Composable
private fun SliderToolbarSetting(
    label: @Composable (Int) -> String,
    initialValue: Int,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    onReset: () -> Unit,
    onValueChangeFinished: (Int) -> Unit
) {
    var currentValue by remember { mutableIntStateOf(initialValue) }

    LaunchedEffect(initialValue) {
        currentValue = initialValue
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label(currentValue),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 16.dp)
            )
            Spacer(Modifier.weight(1f))
            IconButton(onClick = onReset) {
                Icon(
                    imageVector = Icons.Default.Restore,
                    contentDescription = stringResource(R.string.reset),
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }

        Slider(
            value = currentValue.toFloat(),
            onValueChange = { newValue ->
                currentValue = newValue.roundToInt()
            },
            onValueChangeFinished = { onValueChangeFinished(currentValue) },
            valueRange = valueRange,
            steps = steps,
            colors = AppObjectsColors.sliderColors(
                backgroundColor = MaterialTheme.colorScheme.background
            ),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}
