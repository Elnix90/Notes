package org.elnix.notes.ui.helpers.toolbars

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
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
import org.elnix.notes.data.helpers.GlobalNotesActions
import org.elnix.notes.data.helpers.ToolBars
import org.elnix.notes.data.helpers.globalActionColor
import org.elnix.notes.data.helpers.globalActionIcon
import org.elnix.notes.data.helpers.globalActionName
import org.elnix.notes.data.settings.stores.ToolbarItemState
import org.elnix.notes.data.settings.stores.ToolbarItemsSettingsStore
import org.elnix.notes.ui.helpers.TextDivider
import org.elnix.notes.ui.theme.adjustBrightness

@SuppressLint("MutableCollectionMutableState")
@Composable
fun ToolbarItemsEditor(
    ctx: Context,
    toolbar: ToolBars,
    enabled: Boolean = true,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    onDismiss: (() -> Unit)? = null
) {
    val scope = rememberCoroutineScope()


    val selectedItemsFlow = remember { ToolbarItemsSettingsStore.getToolbarItemsFlow(ctx, toolbar) }
    val selectedItems by selectedItemsFlow.collectAsState(initial = emptyList())
    var showDialog by remember { mutableStateOf(false) }

    val allActions = GlobalNotesActions.entries.toList()
    val selectedActions = selectedItems.toSet()

    var toolbarItems by remember {
        mutableStateOf(
            allActions.map { action ->
                ToolbarItemState(action, action in selectedActions)
            }.toMutableList()
        )
    }

    LaunchedEffect(selectedItems) {
        toolbarItems = allActions.map { action ->
            ToolbarItemState(action, action in selectedItems)
        }.toMutableList()
    }


    val reorderState = rememberReorderableLazyListState(
        onMove = { from, to ->
            toolbarItems = toolbarItems.toMutableList().apply {
                add(to.index, removeAt(from.index))
            }
        }
    )


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
            text = "Edit toolbar: ${toolbar.name}",
            color = MaterialTheme.colorScheme.onSurface.copy(if (enabled) 1f else 0.5f)
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                onDismiss?.invoke()
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            ToolbarItemsSettingsStore.setToolbarItems(
                                ctx,
                                toolbar,
                                toolbarItems.filter { it.enabled }.map { it.action }
                            )
                        }
                        onDismiss?.invoke()
                    }
                ) {
                    Text(text = "Save")
                }
            },
            title = { Text(text = "Configure Toolbar: ${toolbar.name}") },
            text = {
                LazyColumn(
                    state = reorderState.listState,
                    modifier = Modifier
                        .detectReorderAfterLongPress(reorderState)
                        .reorderable(reorderState)
                ) {
                    items(toolbarItems.size, key = { toolbarItems[it].action.name }) { index ->
                        val item = toolbarItems[index]
                        val action = item.action

                        val isEnabled = when(toolbar) {
                            ToolBars.SELECT -> action != GlobalNotesActions.DESELECT_ALL
                            ToolBars.SEPARATOR -> false
                            ToolBars.TAGS -> false
                            ToolBars.QUICK_ACTIONS -> action != GlobalNotesActions.SETTINGS
                        }

                        ReorderableItem(state = reorderState, key = action.name) { isDragging ->
                            val scale by animateFloatAsState(if (isDragging) 1.03f else 1f)
                            val elevation by animateDpAsState(if (isDragging) 16.dp else 0.dp)
                            val bgColor =
                                if (isDragging) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                else MaterialTheme.colorScheme.surfaceVariant

                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .scale(scale)
                                    .background(bgColor, RoundedCornerShape(12.dp)),
                                elevation = androidx.compose.material3.CardDefaults.elevatedCardElevation(elevation),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Checkbox(
                                        enabled = isEnabled,
                                        checked = item.enabled,
                                        onCheckedChange = { checked ->
                                            toolbarItems = toolbarItems.toMutableList().apply {
                                                this[index] = this[index].copy(enabled = checked)
                                            }
                                        }
                                    )
                                    if (action != GlobalNotesActions.SPACER1 && action != GlobalNotesActions.SPACER2 && action != GlobalNotesActions.SPACER3) {

                                        Icon(
                                            imageVector = globalActionIcon(action),
                                            contentDescription = "",
                                            tint = globalActionColor(action),
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                        Text(
                                            text = globalActionName(ctx, action),
                                            style = MaterialTheme.typography.bodyLarge,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Icon(
                                            imageVector = Icons.Default.DragHandle,
                                            contentDescription = "Drag handle",
                                            modifier = Modifier.detectReorder(reorderState)
                                        )

                                    } else {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                        ){
                                            TextDivider(
                                                stringResource(R.string.spacer),
                                                backgroundColor = MaterialTheme.colorScheme.surface.adjustBrightness(
                                                    0.7f
                                                ),
                                                thickness = 5.dp
                                            )
                                            Icon(
                                                imageVector = Icons.Default.DragHandle,
                                                contentDescription = "Drag handle",
                                                modifier = Modifier
                                                    .detectReorder(reorderState)
                                                    .size(24.dp)
                                                    .padding(start = 8.dp),
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}
