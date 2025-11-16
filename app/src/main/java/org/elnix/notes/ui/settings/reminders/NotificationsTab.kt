package org.elnix.notes.ui.settings.reminders

import android.content.Context
import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.CardDefaults.elevatedCardElevation
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorder
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.elnix.notes.R
import org.elnix.notes.data.settings.stores.NotificationActionSetting
import org.elnix.notes.data.settings.stores.NotificationActionType
import org.elnix.notes.data.settings.stores.NotificationsSettingsStore
import org.elnix.notes.data.settings.stores.notificationActionName
import org.elnix.notes.ui.helpers.settings.SettingsLazyHeader
import org.elnix.notes.ui.theme.AppObjectsColors

@Composable
fun NotificationsCustomisationTab(
    ctx: Context,
    scope: CoroutineScope,
    onBack: (() -> Unit)
) {
    val sourceList by NotificationsSettingsStore.getSettingsFlow(ctx).collectAsState(initial = emptyList())
    val uiList = remember { mutableStateListOf<NotificationActionSetting>() }

    // Sync sourceList -> uiList
    LaunchedEffect(sourceList) {
        if (sourceList.size != uiList.size || !sourceList.withIndex().all { (i, item) -> uiList.getOrNull(i) == item }) {
            uiList.clear()
            uiList.addAll(sourceList)
            Log.d("Notifications", "sync source -> ui (size=${uiList.size})")
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
            }
        },
        onDragEnd = { _, _ ->
            scope.launch { NotificationsSettingsStore.setActionOrder(ctx, uiList.map { it.actionType }) }
        }
    )

    val cardColor = MaterialTheme.colorScheme.surface

    SettingsLazyHeader(
        title = stringResource(R.string.notifications),
        onBack = onBack,
        helpText = stringResource(R.string.notifications_help_text),
        onReset = {
            scope.launch { NotificationsSettingsStore.resetAll(ctx) }
        },
        reorderState = reorderState
    ) {
        items(
            items = uiList,
            key = { it.actionType.name }
        ) { action ->

            ReorderableItem(state = reorderState, key = action.actionType.name) { isDraggingItem ->
                val scale by animateFloatAsState(if (isDraggingItem) 1.03f else 1f)
                val elevation by animateDpAsState(if (isDraggingItem) 16.dp else 0.dp)

                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(scale)
                        .padding(vertical = 4.dp)
                        .background(cardColor, RoundedCornerShape(12.dp))
                        .padding(vertical = 12.dp),
                    elevation = elevatedCardElevation(elevation)
                ) {

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(cardColor)
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Checkbox(
                            checked = action.enabled,
                            onCheckedChange = { enabled ->
                                scope.launch { NotificationsSettingsStore.setEnabled(ctx, action.actionType, enabled) }
                            },
                            colors = AppObjectsColors.checkboxColors()
                        )

                        Text(
                            text = notificationActionName(ctx, action.actionType),
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Spacer(Modifier.weight(1f))

                        if (action.actionType == NotificationActionType.SNOOZE) {
                            var snoozeText by remember { mutableStateOf(action.snoozeMinutes.toString()) }
                            OutlinedTextField(
                                value = snoozeText,
                                onValueChange = {
                                    snoozeText = it
                                    snoozeText.toIntOrNull()?.let { minutes ->
                                        scope.launch { NotificationsSettingsStore.setSnoozeDuration(ctx, minutes) }
                                    }
                                },
                                label = { Text("Snooze (min)") },
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.width(100.dp),
                                colors = AppObjectsColors.outlinedTextFieldColors(
                                    backgroundColor = cardColor
                                )
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.DragHandle,
                            contentDescription = stringResource(R.string.reorder_handle),
                            tint = if (isDraggingItem) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.detectReorder(reorderState)
                        )
                    }
                }
            }
        }
    }
}
