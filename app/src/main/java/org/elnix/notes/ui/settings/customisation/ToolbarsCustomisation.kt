package org.elnix.notes.ui.settings.customisation

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
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
import org.elnix.notes.ui.helpers.SwitchRow
import org.elnix.notes.ui.helpers.TextDivider
import org.elnix.notes.ui.helpers.settings.SettingsTitle
import org.elnix.notes.ui.helpers.toolbars.ToolbarItemsEditor
import org.elnix.notes.ui.helpers.toolbars.ToolbarsSettingsRow
import org.elnix.notes.ui.helpers.toolbars.UnifiedToolbar
import org.elnix.notes.ui.theme.AppObjectsColors
import kotlin.math.roundToInt

@Composable
fun ToolbarsCustomisationTab(ctx: Context, scope: CoroutineScope, onBack: (() -> Unit)) {

    val floatingToolbars by ToolbarsSettingsStore.getFloatingToolbars(ctx).collectAsState(initial = true)

    val border by ToolbarsSettingsStore.getToolbarsBorder(ctx).collectAsState(initial = 2)
    val corner by ToolbarsSettingsStore.getToolbarsCorner(ctx).collectAsState(initial = 50)
    val paddingLeft by ToolbarsSettingsStore.getToolbarsPaddingLeft(ctx).collectAsState(initial = 16)
    val paddingRight by ToolbarsSettingsStore.getToolbarsPaddingRight(ctx).collectAsState(initial = 16)
    val spacing by ToolbarsSettingsStore.getToolbarsSpacing(ctx).collectAsState(initial = 8)

    val maxPadding = 100

    val toolbarsFlow = remember { ToolbarsSettingsStore.getToolbarsFlow(ctx) }
    val toolbars by toolbarsFlow.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(WindowInsets.systemBars.asPaddingValues())
            .padding(horizontal = 16.dp)
            .imePadding()
    ) {

        Surface(color = MaterialTheme.colorScheme.background, tonalElevation = 3.dp) {
            SettingsTitle(title = stringResource(R.string.toolbars)) { onBack() }
            Spacer(Modifier.height(20.dp))
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(bottom = 400.dp)
        ) {

            toolbars.filter { it.toolbar != ToolBars.SEPARATOR }.forEach { bar ->
                item {
                    UnifiedToolbar(
                        ctx = ctx,
                        toolbar = bar.toolbar,
                        scrollState = rememberScrollState(),
                        isSearchExpanded = false,
                        color = bar.color,
                        borderColor = bar.borderColor,
                        ghosted = true
                    )
                }
            }


            item { TextDivider(stringResource(R.string.toolbars_appearance)) }


            item {
                SwitchRow(
                    floatingToolbars,
                    stringResource(R.string.floating_toolbars)
                ) {
                    scope.launch { ToolbarsSettingsStore.setFloatingToolbars(ctx, it) }
                }
            }

            // Border slider
            item {
                SliderToolbarSetting(
                    label = { value ->
                        if (value > 0)
                            "${stringResource(R.string.toolbars_border)}:  $value dp"
                        else stringResource(R.string.no_border)
                    },
                    initialValue = border,
                    valueRange = 0f..10f,
                    steps = 9,
                    onReset = { scope.launch { ToolbarsSettingsStore.setToolbarsBorder(ctx, 2) } },
                    onValueChangeFinished = { v ->
                        scope.launch { ToolbarsSettingsStore.setToolbarsBorder(ctx, v) }
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
                    initialValue = corner,
                    valueRange = 0f..50f,
                    steps = 49,
                    onReset = { scope.launch { ToolbarsSettingsStore.setToolbarsCorner(ctx, 50) } },
                    onValueChangeFinished = { v ->
                        scope.launch { ToolbarsSettingsStore.setToolbarsCorner(ctx, v) }
                    }
                )
            }

            // Padding left
            item {
                SliderToolbarSetting(
                    label = { value ->
                        "${stringResource(R.string.padding)} ${stringResource(R.string.left)}: $value px"
                    },
                    initialValue = paddingLeft,
                    valueRange = 0f..maxPadding.toFloat(),
                    steps = maxPadding - 1,
                    onReset = {
                        scope.launch {
                            ToolbarsSettingsStore.setToolbarsPaddingLeft(
                                ctx,
                                16
                            )
                        }
                    },
                    onValueChangeFinished = { v ->
                        scope.launch { ToolbarsSettingsStore.setToolbarsPaddingLeft(ctx, v) }
                    }
                )
            }

            item {
                // Padding right
                SliderToolbarSetting(
                    label = { value ->
                        "${stringResource(R.string.padding)} ${stringResource(R.string.right)}: $value px"
                    },
                    initialValue = paddingRight,
                    valueRange = 0f..maxPadding.toFloat(),
                    steps = maxPadding - 1,
                    onReset = {
                        scope.launch {
                            ToolbarsSettingsStore.setToolbarsPaddingRight(
                                ctx,
                                16
                            )
                        }
                    },
                    onValueChangeFinished = { v ->
                        scope.launch { ToolbarsSettingsStore.setToolbarsPaddingRight(ctx, v) }
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
            item { ToolbarsSettingsRow(ctx) }
            item { ToolbarItemsEditor(ctx, ToolBars.SELECT) }
            item { ToolbarItemsEditor(ctx, ToolBars.TAGS) }
            item { ToolbarItemsEditor(ctx, ToolBars.QUICK_ACTIONS) }
        }
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
