package org.elnix.notes.ui.helpers.toolbars

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorder
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import org.elnix.notes.R
import org.elnix.notes.data.helpers.GlobalActionIcon
import org.elnix.notes.data.helpers.GlobalNotesActions
import org.elnix.notes.data.helpers.TagItem
import org.elnix.notes.data.helpers.ToolBars
import org.elnix.notes.data.helpers.globalActionColor
import org.elnix.notes.data.helpers.toolbarName
import org.elnix.notes.data.settings.stores.ToolbarItemState
import org.elnix.notes.data.settings.stores.ToolbarItemsSettingsStore
import org.elnix.notes.ui.helpers.TextDivider
import org.elnix.notes.ui.helpers.tags.TagBubble
import org.elnix.notes.ui.theme.AppObjectsColors
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

    val selectedToolbarItemsFlow = remember { ToolbarItemsSettingsStore.getToolbarItemsFlow(ctx, toolbar) }
    val selectedToolbarItems by selectedToolbarItemsFlow.collectAsState(initial = emptyList())

    var showDialog by remember { mutableStateOf(false) }


    var toolbarItems by remember { mutableStateOf(selectedToolbarItems.toMutableList()) }

    LaunchedEffect(selectedToolbarItems) {
        toolbarItems = selectedToolbarItems.toMutableList()
    }


    val reorderState = rememberReorderableLazyListState(
        onMove = { from, to ->
            toolbarItems = toolbarItems.toMutableList().apply {
                add(to.index, removeAt(from.index))
            }
        }
    )

    var editAction by remember { mutableStateOf<ToolbarItemState?>(null) }

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
            text = "${stringResource(R.string.edit_toolbar)}: ${toolbarName(toolbar)}",
            color = MaterialTheme.colorScheme.onSurface.copy(if (enabled) 1f else 0.5f),
            modifier = Modifier.weight(1f),
            maxLines = Int.MAX_VALUE,
            softWrap = true
        )

        Button(
            onClick = {
                scope.launch {
                    toolbarItems.forEach { item ->
                        ToolbarItemsSettingsStore.updateToolbarItemColor(
                            ctx = ctx,
                            toolbar = toolbar,
                            action = item.action,
                            newColor = null
                        )
                    }
                }
            },
            enabled = toolbarItems.any { it.color != null },
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
            onDismissRequest = {
                showDialog = false
                onDismiss?.invoke()
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            ToolbarItemsSettingsStore.setToolbarItems(ctx, toolbar, toolbarItems)
                        }
                        showDialog = false
                        onDismiss?.invoke()
                    },
                    colors = AppObjectsColors.buttonColors()
                ) {
                    Text(text = stringResource(R.string.ok))
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
                            ToolBars.SELECT -> true
                            ToolBars.SEPARATOR -> false
                            ToolBars.TAGS -> true
                            ToolBars.QUICK_ACTIONS -> action != GlobalNotesActions.SETTINGS
                        }

                        ReorderableItem(state = reorderState, key = action.name) { isDragging ->
                            val scale by animateFloatAsState(if (isDragging) 1.03f else 1f)
                            val elevation by animateDpAsState(if (isDragging) 16.dp else 0.dp)
                            val bgColor = MaterialTheme.colorScheme.surface.adjustBrightness(0.7f)

                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .scale(scale),
                                elevation = elevatedCardElevation(elevation),
                                colors = CardDefaults.cardColors(
                                    containerColor = bgColor
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Checkbox(
                                        enabled = isEnabled,
                                        checked = item.enabled,
                                        onCheckedChange = { checked ->
                                            toolbarItems = toolbarItems.toMutableList().apply {
                                                this[index] = this[index].copy(enabled = checked)
                                            }
                                        },
                                    )
                                    Spacer(Modifier.weight(1f))
                                    when (action) {
                                        GlobalNotesActions.TAGS -> TagBubble(
                                            TagItem(
                                                name = stringResource(R.string.tags),
                                                color = Color.Blue
                                            ), ghostMode = true
                                        )

                                        GlobalNotesActions.SPACER1, GlobalNotesActions.SPACER2, GlobalNotesActions.SPACER3 -> {
                                            Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier
                                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                                    .width(150.dp)
                                            ) {
                                                TextDivider(
                                                    stringResource(R.string.spacer),
                                                    backgroundColor = MaterialTheme.colorScheme.surface.adjustBrightness(
                                                        0.7f
                                                    ),
                                                    thickness = 5.dp,
                                                    modifier = Modifier.wrapContentWidth()
                                                )
                                            }
                                        }

                                        else -> {
                                            GlobalActionIcon(
                                                ctx = ctx,
                                                color = item.color,
                                                action = action,
                                                showButtonLabel = item.showLabel,
                                                onClick = {
                                                    toolbarItems = toolbarItems.toMutableList().apply {
                                                        set(
                                                            index,
                                                            this[index].copy(showLabel = !item.showLabel)
                                                        )
                                                    }
                                                }
                                            )

                                            Spacer(Modifier.weight(1f))

                                            IconButton(
                                                onClick = {
                                                    scope.launch {
                                                        ToolbarItemsSettingsStore.setToolbarItems(
                                                            ctx,
                                                            toolbar,
                                                            toolbarItems
                                                        )
                                                    }
                                                    editAction = item
                                                },
                                                colors = AppObjectsColors.iconButtonColors(),
                                                shape = CircleShape
                                            ){
                                                Icon(
                                                    imageVector = Icons.Default.ColorLens,
                                                    contentDescription = stringResource(R.string.toolbar_color),
                                                )
                                            }
                                        }
                                    }

                                    Spacer(Modifier.weight(1f))
                                    Icon(
                                        imageVector = Icons.Default.DragHandle,
                                        contentDescription = "Drag handle",
                                        modifier = Modifier.detectReorder(reorderState),
                                        tint = MaterialTheme.colorScheme.outline
                                    )
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

    if (editAction != null) {
        val actionToEdit = editAction!!
        val defaultColor = globalActionColor(actionToEdit.action)
        ToolbarItemColorSelectorDialog(
            item = actionToEdit,
            defaultColor = defaultColor,
            onDismiss = { editAction = null }
        ) { color ->
            if (color != defaultColor.toArgb()) {
                scope.launch {
                    ToolbarItemsSettingsStore.updateToolbarItemColor(
                        ctx = ctx,
                        toolbar = toolbar,
                        action = actionToEdit.action,
                        newColor = Color(color)
                    )
                }
            }
            editAction = null
        }
    }
}
