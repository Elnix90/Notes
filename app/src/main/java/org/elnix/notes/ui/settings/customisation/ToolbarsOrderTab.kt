package org.elnix.notes.ui.settings.customisation


import android.content.Context
import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CardDefaults.elevatedCardElevation
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorder
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.elnix.notes.R
import org.elnix.notes.Routes
import org.elnix.notes.data.helpers.ToolBars
import org.elnix.notes.data.helpers.toolbarName
import org.elnix.notes.data.settings.stores.ToolbarSetting
import org.elnix.notes.data.settings.stores.ToolbarsSettingsStore
import org.elnix.notes.ui.helpers.TextDivider
import org.elnix.notes.ui.helpers.toolbars.SliderToolbarSetting
import org.elnix.notes.ui.helpers.toolbars.ToolbarColorSelectorDialog
import org.elnix.notes.ui.helpers.toolbars.UnifiedToolbar
import org.elnix.notes.ui.settings.SettingsLazyHeader
import org.elnix.notes.ui.theme.AppObjectsColors
import org.elnix.notes.ui.theme.LocalExtraColors
import org.elnix.notes.ui.theme.adjustBrightness

@Composable
fun ToolbarsOrderTab(
    ctx: Context,
    scope: CoroutineScope,
    navController: NavController,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    var showColorPickerDialog by remember { mutableStateOf(false) }
    var editToolbar by remember { mutableStateOf<ToolbarSetting?>(null) }

    val sourceList by remember { ToolbarsSettingsStore.getToolbarsFlow(ctx) }
        .collectAsState(initial = ToolbarsSettingsStore.defaultList)

    val uiList = remember { mutableStateListOf<ToolbarSetting>() }

    val toolbarsSpacing by ToolbarsSettingsStore.getToolbarsSpacing(ctx).collectAsState(initial = 8)


    // Sync sourceList -> uiList safely and only when changed
    LaunchedEffect(sourceList) {
        if (sourceList.size != uiList.size || !sourceList.withIndex().all { (i, item) -> uiList.getOrNull(i) == item }) {
            uiList.clear()
            uiList.addAll(sourceList)
            Log.i("Toolbars", "sync source -> ui (size=${uiList.size})")
        } else {
            Log.d("Toolbars", "source == ui; no sync needed")
        }
    }

    val reorderState = rememberReorderableLazyListState(
        onMove = { from, to ->
            if (from.index in uiList.indices && to.index in 0..uiList.size) {
                val tmp = uiList.toMutableList()
                val item = tmp.removeAt(from.index)
                tmp.add(to.index, item)
                uiList.clear()
                uiList.addAll(tmp)
                Log.d("Toolbars", "onMove from=${from.index} to=${to.index} newSize=${uiList.size}")
            } else {
                Log.w("Toolbars", "invalid move indices from=${from.index} to=${to.index} size=${uiList.size}")
            }
        },
        onDragEnd = { _,_ ->
            Log.i("Toolbars", "drag ended — committing changes (size=${uiList.size})")
            scope.launch { ToolbarsSettingsStore.setToolbars(ctx, uiList) }
        }
    )

    val cardColor = MaterialTheme.colorScheme.surface.adjustBrightness(0.6f)

    SettingsLazyHeader(
        title = stringResource(R.string.toolbars_order),
        onBack = onBack,
        helpText = stringResource(R.string.toolbars_order_help_explanation),
        onReset = {
            scope.launch {
                ToolbarsSettingsStore.resetAll(ctx)
            }
        },
        reorderState = reorderState
    ) {
        items(
            items = uiList,
            key = { it.toolbar.name }
        ) { bar ->

            ReorderableItem(state = reorderState, key = bar.toolbar.name) { isDraggingItem ->
                val scale by animateFloatAsState(if (isDraggingItem) 1.03f else 1f)
                val elevation by animateDpAsState(if (isDraggingItem) 16.dp else 0.dp)
                val bgColor = cardColor.copy(alpha = if (isDraggingItem) 0.2f else 1f)
                val borderColor = MaterialTheme.colorScheme.surface.copy(alpha = if (isDraggingItem) 0.2f else 1f)

                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(scale)
                        .padding(vertical = 4.dp)
                        .background(bgColor, RoundedCornerShape(12.dp))
                        .border(BorderStroke(2.dp, borderColor), RoundedCornerShape(12.dp))
                        .padding(vertical = 15.dp),
                    elevation = elevatedCardElevation(elevation)
                ) {

                    if (bar.toolbar != ToolBars.SEPARATOR) {
                        val index = uiList.indexOfFirst { it.toolbar == bar.toolbar }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(cardColor),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(15.dp)
                        ) {
                            UnifiedToolbar(
                                ctx = ctx,
                                toolbar = bar.toolbar,
                                scrollState = scrollState,
                                isSearchExpanded = false,
                                color = bar.color,
                                borderColor = bar.borderColor,
                                borderWidth = bar.borderWidth,
                                borderRadius = bar.borderRadius,
                                elevation = bar.elevation,
                                paddingLeft = bar.leftPadding,
                                paddingRight = bar.rightPadding,
                                ghosted = true
                            )
                            HorizontalDivider(Modifier.padding(horizontal = 15.dp), color = MaterialTheme.colorScheme.outline)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(cardColor)
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = bar.enabled,
                                    enabled = bar.toolbar != ToolBars.QUICK_ACTIONS,
                                    onCheckedChange = { checked ->
                                        uiList[index] = bar.copy(enabled = checked)
                                        scope.launch { ToolbarsSettingsStore.setToolbars(ctx, uiList) }
                                    }
                                )

                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            ToolbarsSettingsStore.setToolbars(ctx, uiList)
                                            navController.navigate("${Routes.Settings.CustomisationSub.TOOLBAR_EDITOR}?toolbar=${bar.toolbar.name}")
                                        }
                                    },
                                    colors = AppObjectsColors.iconButtonColors(),
                                    shape = CircleShape
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = stringResource(R.string.toolbar_customization),
                                    )
                                }

                                Text(
                                    text = toolbarName(bar.toolbar),
                                    modifier = Modifier.wrapContentWidth(),
                                    style = MaterialTheme.typography.bodyLarge
                                )

                                Spacer(Modifier.weight(1f))

                                IconButton(
                                    onClick = {
                                        scope.launch { ToolbarsSettingsStore.setToolbars(ctx, uiList) }
                                        editToolbar = bar
                                        showColorPickerDialog = true
                                    },
                                    colors = AppObjectsColors.iconButtonColors(),
                                    shape = CircleShape
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ColorLens,
                                        contentDescription = stringResource(R.string.toolbar_color),
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            ToolbarsSettingsStore.resetToolbar(ctx, uiList[index].toolbar)
                                        }
                                    },
                                    colors = AppObjectsColors.iconButtonColors(),
                                    shape = CircleShape
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Restore,
                                        contentDescription = stringResource(R.string.reset),
                                    )
                                }

                                Spacer(Modifier.weight(1f))

                                Icon(
                                    imageVector = Icons.Default.DragHandle,
                                    contentDescription = stringResource(R.string.reorder_handle),
                                    tint = if (isDraggingItem) LocalExtraColors.current.select else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.detectReorder(reorderState)
                                )
                            }
                        }

                    } else {
                        TextDivider(
                            stringResource(R.string.notes_display),
                            backgroundColor = MaterialTheme.colorScheme.surface.adjustBrightness(0.7f),
                            thickness = 5.dp,
                            modifier = Modifier
                                .background(cardColor)
                                .padding(15.dp)
                                .height(50.dp)
                        )
                    }
                }
            }
        }

        item { TextDivider(stringResource(R.string.toolbars_spacing)) }

        item {
            // Toolbars spacing
            SliderToolbarSetting(
                label = { value ->
                    "${stringResource(R.string.toolbars_spacing)}: $value px"
                },
                initialValue = toolbarsSpacing,
                valueRange = 0f..100.toFloat(),
                steps = 99,
                onReset = { scope.launch { ToolbarsSettingsStore.setToolbarsSpacing(ctx, 8) } },
                onValueChangeFinished = { v ->
                    scope.launch { ToolbarsSettingsStore.setToolbarsSpacing(ctx, v) }
                }
            )
        }
    }
    if (showColorPickerDialog && editToolbar != null) {
        val toolbarToEdit = editToolbar!!
        ToolbarColorSelectorDialog(
            toolbar = toolbarToEdit,
            onDismiss = { showColorPickerDialog = false }
        ) { color, borderColor ->
            scope.launch {
                ToolbarsSettingsStore.updateToolbarColor(
                    ctx = ctx,
                    toolbar = toolbarToEdit.toolbar,
                    color = Color(color),
                    borderColor = Color(borderColor)
                )
            }
            showColorPickerDialog = false
        }
    }
}
