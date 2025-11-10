package org.elnix.notes.ui.settings.customisation


import android.content.Context
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import org.elnix.notes.R
import org.elnix.notes.Routes
import org.elnix.notes.data.helpers.ToolBars
import org.elnix.notes.data.helpers.toolbarName
import org.elnix.notes.data.settings.stores.ToolbarSetting
import org.elnix.notes.data.settings.stores.ToolbarsSettingsStore
import org.elnix.notes.ui.helpers.TextDivider
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
    val toolbarsFlow = remember { ToolbarsSettingsStore.getToolbarsFlow(ctx) }
    val toolbars by toolbarsFlow.collectAsState(initial = emptyList())
    var list by remember { mutableStateOf(toolbars) }

    LaunchedEffect(toolbars) {
        list = toolbars
    }

    val reorderState = rememberReorderableLazyListState(
        onMove = { from, to ->
            list = list.toMutableList().apply {
                add(to.index, removeAt(from.index))
            }
            scope.launch { ToolbarsSettingsStore.setToolbars(ctx, list) }
        }
    )

    var showColorPickerDialog by remember { mutableStateOf(false) }
    var editToolbar by remember { mutableStateOf<ToolbarSetting?>(null) }


    SettingsLazyHeader(
        title = stringResource(R.string.toolbars_order),
        onBack = onBack
    ) {
        item {
            LazyColumn(
                state = reorderState.listState,
                modifier = Modifier
                    .reorderable(reorderState)
                    .detectReorderAfterLongPress(reorderState)
            ) {
                items(list.size, key = { list[it].toolbar.name }) { index ->
                    val bar = list[index]

                    val color = bar.color ?: MaterialTheme.colorScheme.surface.adjustBrightness(0.7f)
                    val borderColor = bar.borderColor ?: color.adjustBrightness(3f)

                    ReorderableItem(state = reorderState, key = bar.toolbar.name) { isDragging ->
                        val scale by animateFloatAsState(if (isDragging) 1.03f else 1f)
                        val elevation by animateDpAsState(if (isDragging) 16.dp else 0.dp)

                        val bgColor =
                            if (isDragging) color.copy(alpha = 0.2f)
                            else color

                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .scale(scale)
                                .background(bgColor, RoundedCornerShape(12.dp))
                                .border(
                                    BorderStroke(1.dp, borderColor),
                                    RoundedCornerShape(12.dp)
                                )
                                .detectReorder(reorderState),
                            elevation = elevatedCardElevation(elevation)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                UnifiedToolbar(
                                    ctx = ctx,
                                    toolbar = bar.toolbar,
                                    scrollState = rememberScrollState(),
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
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            MaterialTheme.colorScheme.surface.adjustBrightness(
                                                0.7f
                                            )
                                        )
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    if (bar.toolbar != ToolBars.SEPARATOR) {

                                        Checkbox(
                                            checked = bar.enabled,
                                            enabled = bar.toolbar != ToolBars.QUICK_ACTIONS,
                                            onCheckedChange = { checked ->
                                                list = list.toMutableList().apply {
                                                    set(index, bar.copy(enabled = checked))
                                                }
                                            }
                                        )

                                        IconButton(
                                            onClick = {
                                                scope.launch {
                                                    ToolbarsSettingsStore.setToolbars(
                                                        ctx,
                                                        list
                                                    )
                                                    navController.navigate("${Routes.Settings.CustomisationSub.TOOLBAR_EDITOR}/${bar.toolbar}")
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
                                            modifier = Modifier.weight(1f),
                                            style = MaterialTheme.typography.bodyLarge
                                        )

                                        IconButton(
                                            onClick = {
                                                scope.launch {
                                                    ToolbarsSettingsStore.setToolbars(
                                                        ctx,
                                                        list
                                                    )
                                                }
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
                                            tint = if (isDragging)
                                                LocalExtraColors.current.select
                                            else
                                                MaterialTheme.colorScheme.outline,
                                            modifier = Modifier.detectReorder(reorderState)
                                        )
                                    } else {
                                        TextDivider(
                                            stringResource(R.string.notes_display),
                                            backgroundColor = MaterialTheme.colorScheme.surface.adjustBrightness(
                                                0.7f
                                            ),
                                            thickness = 5.dp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
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
