package org.elnix.notes.ui.settings.customisation


import android.content.Context
import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import org.elnix.notes.data.settings.stores.UiSettingsStore
import org.elnix.notes.ui.helpers.TextDivider
import org.elnix.notes.ui.helpers.UserValidation
import org.elnix.notes.ui.helpers.colors.ColorSelectorEntry
import org.elnix.notes.ui.helpers.colors.UnifiedColorsSelectorDialog
import org.elnix.notes.ui.helpers.settings.SettingsLazyHeader
import org.elnix.notes.ui.helpers.toolbars.UnifiedToolbar
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

    var showWarningAboutUnabilityToAccessSettings by remember { mutableStateOf(false) }

    val sourceList by remember { ToolbarsSettingsStore.getToolbarsFlow(ctx) }
        .collectAsState(initial = ToolbarsSettingsStore.defaultList)

    val uiList = remember { mutableStateListOf<ToolbarSetting>() }

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

    val cardColor = MaterialTheme.colorScheme.surface

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

                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(scale)
                        .clip(RoundedCornerShape(12.dp))
                        .background(cardColor)
                        .clickable {
                            if (bar.toolbar != ToolBars.SEPARATOR) {
                                scope.launch {
                                    ToolbarsSettingsStore.setToolbars(ctx, uiList)
                                    navController.navigate("${Routes.Settings.CustomisationSub.TOOLBAR_EDITOR}?toolbar=${bar.toolbar.name}")
                                }
                            }
                        }
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
                                toolbar = bar,
                                scrollState = scrollState,
                                isMultiSelect = false,
                                isSearchExpanded = false,
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
                                    onCheckedChange = { checked ->

                                        if (bar.toolbar != ToolBars.QUICK_ACTIONS || !bar.enabled){
                                            uiList[index] = bar.copy(enabled = checked)
                                            scope.launch {
                                                ToolbarsSettingsStore.setToolbars(
                                                    ctx,
                                                    uiList
                                                )
                                            }
                                        } else {
                                            showWarningAboutUnabilityToAccessSettings = true
                                        }

                                        if (bar.toolbar == ToolBars.QUICK_ACTIONS || bar.enabled) {
                                            scope.launch {
                                                UiSettingsStore.setShowBottomDeleteButton(
                                                    ctx,
                                                    false
                                                )
                                            }
                                        }
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
                                    text = toolbarName(bar),
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
                            backgroundColor = cardColor,
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
    }
    if (showColorPickerDialog && editToolbar != null) {
        val toolbarToEdit = editToolbar!!

        val defaultSurfaceColor = MaterialTheme.colorScheme.surface
        val defaultBorderColor = defaultSurfaceColor.adjustBrightness(3f)

        UnifiedColorsSelectorDialog(
            titleDialog = stringResource(R.string.toolbar_colors),
            entries = listOf(
                ColorSelectorEntry(
                    label = stringResource(R.string.toolbar_color),
                    defaultColor = defaultSurfaceColor,
                    initialColor = toolbarToEdit.color ?: defaultSurfaceColor
                ),
                ColorSelectorEntry(
                    label = stringResource(R.string.toolbar_border),
                    defaultColor = defaultBorderColor,
                    initialColor = toolbarToEdit.borderColor ?: defaultBorderColor
                )
            ),
            onDismiss = { showColorPickerDialog = false }
        ) { colors ->
            val color = colors[0]
            val borderColor = colors[1]
            scope.launch {
                ToolbarsSettingsStore.updateToolbarColor(
                    ctx = ctx,
                    toolbar = toolbarToEdit.toolbar,
                    color = color,
                    borderColor = borderColor
                )
            }
            showColorPickerDialog = false
        }
    }

    if (showWarningAboutUnabilityToAccessSettings) {
        UserValidation(
            title = stringResource(R.string.remove_the_quick_actions_toolbar),
            message = stringResource(R.string.this_may_remove_you_the_ability_to_access_settings),
            onCancel = { showWarningAboutUnabilityToAccessSettings = false }
        ) {
            // Update uiList by disabling the quick actions toolbar
            val updatedList = uiList.map {
                if (it.toolbar == ToolBars.QUICK_ACTIONS) it.copy(enabled = false) else it
            }
            uiList.clear()
            uiList.addAll(updatedList)

            scope.launch {
                ToolbarsSettingsStore.setToolbars(
                    ctx,
                    uiList
                )
                UiSettingsStore.setShowBottomDeleteButton(ctx, true)
            }
            showWarningAboutUnabilityToAccessSettings = false
        }
    }
}
