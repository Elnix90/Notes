package org.elnix.notes.ui.helpers.toolbars

import android.content.Context
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults.elevatedCardElevation
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorder
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import org.elnix.notes.R
import org.elnix.notes.data.helpers.ToolBars
import org.elnix.notes.data.settings.stores.ToolbarSetting
import org.elnix.notes.data.settings.stores.ToolbarsSettingsStore
import org.elnix.notes.ui.helpers.TextDivider
import org.elnix.notes.ui.theme.AppObjectsColors
import org.elnix.notes.ui.theme.LocalExtraColors
import org.elnix.notes.ui.theme.adjustBrightness

@Composable
fun ToolbarsSettingsRow(
    ctx: Context,
    enabled: Boolean = true,
    backgroundColor: Color = MaterialTheme.colorScheme.surface
) {
    val scope = rememberCoroutineScope()
    val toolbarsFlow = remember { ToolbarsSettingsStore.getToolbarsFlow(ctx) }
    val toolbars by toolbarsFlow.collectAsState(initial = emptyList())
    var list by remember { mutableStateOf(toolbars) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(toolbars) {
        list = toolbars
    }

    val reorderState = rememberReorderableLazyListState(
        onMove = { from, to ->
            list = list.toMutableList().apply {
                add(to.index, removeAt(from.index))
            }
        }
    )

    var showColorPickerDialog by remember { mutableStateOf(false) }
    var editToolbar by remember { mutableStateOf<ToolbarSetting?>(null) }


    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = backgroundColor.copy(if (enabled) 1f else 0.5f),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(enabled) { showDialog = true }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Text(
            text = stringResource(R.string.toolbars_order),
            color = MaterialTheme.colorScheme.onSurface.copy(if (enabled) 1f else 0.5f)
        )

        Spacer(Modifier.weight(1f))

        Button(
            onClick = {
                scope.launch {
                    toolbars.forEach { item ->
                        ToolbarsSettingsStore.updateToolbarColor(
                            ctx = ctx,
                            toolbar = item.toolbar,
                            color = null,
                            borderColor = null
                        )
                    }
                }
            },
            enabled = toolbars.any { it.color != null || it.borderColor != null },
            colors = AppObjectsColors.buttonColors(),
            shape = CircleShape
        ){
            Icon(
                imageVector = Icons.Default.Restore,
                contentDescription = stringResource(R.string.reset_colors),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.reset_colors),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch { ToolbarsSettingsStore.setToolbars(ctx, list) }
                        showDialog = false
                    },
                    colors = AppObjectsColors.buttonColors()
                ) {
                    Text(text = stringResource(R.string.ok))
                }
            },
            title = { Text(text = "Toolbars order") },
            text = {
                LazyColumn(
                    state = reorderState.listState,
                    modifier = Modifier
                        .reorderable(reorderState)
                        .detectReorderAfterLongPress(reorderState)
                ) {
                    items(list.size, key = { list[it].toolbar.name }) { index ->
                        val item = list[index]

                        val color = item.color ?: MaterialTheme.colorScheme.surface.adjustBrightness(0.7f)
                        val borderColor = item.borderColor ?: color.adjustBrightness(3f)

                        ReorderableItem(state = reorderState, key = item.toolbar.name) { isDragging ->
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

                                    if (item.toolbar != ToolBars.SEPARATOR) {

                                        Checkbox(
                                            checked = item.enabled,
                                            enabled = item.toolbar != ToolBars.QUICK_ACTIONS,
                                            onCheckedChange = { checked ->
                                                list = list.toMutableList().apply {
                                                    set(index, item.copy(enabled = checked))
                                                }
                                            }
                                        )


                                        Text(
                                            text = item.toolbar.name,
                                            modifier = Modifier.weight(1f),
                                            style = MaterialTheme.typography.bodyLarge
                                        )

                                        IconButton(
                                            onClick = {
                                                scope.launch { ToolbarsSettingsStore.setToolbars(ctx, list) }
                                                editToolbar = item
                                                showColorPickerDialog = true
                                            },
                                            colors = AppObjectsColors.iconButtonColors(),
                                            shape = CircleShape
                                        ){
                                            Icon(
                                                imageVector = Icons.Default.ColorLens,
                                                contentDescription = stringResource(R.string.toolbar_color),
                                            )
                                        }

                                        Spacer(Modifier.width(8.dp))

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
                                            backgroundColor = MaterialTheme.colorScheme.surface.adjustBrightness(0.7f),
                                            thickness = 5.dp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
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
