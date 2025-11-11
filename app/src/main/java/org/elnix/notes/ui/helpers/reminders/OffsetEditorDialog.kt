package org.elnix.notes.ui.helpers.reminders

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.elnix.notes.R
import org.elnix.notes.data.helpers.OffsetItem
import org.elnix.notes.data.settings.stores.OffsetsSettingsStore
import org.elnix.notes.ui.helpers.UserValidation
import org.elnix.notes.ui.theme.AppObjectsColors

@Composable
fun OffsetEditorDialog(
    initialOffset: OffsetItem? = null,
    scope: CoroutineScope,
    onDismiss: () -> Unit
) {
    val ctx = LocalContext.current
    var showConfirmDelete by remember { mutableStateOf(false) }

    // Convert offset (seconds) to h/m/s
    val totalSeconds = initialOffset?.offset ?: 600
    var days by remember { mutableIntStateOf(totalSeconds / 86400) }
    var hours by remember { mutableIntStateOf(totalSeconds / 3600) }
    var minutes by remember { mutableIntStateOf((totalSeconds % 3600) / 60) }
    var seconds by remember { mutableIntStateOf(totalSeconds % 60) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                text = if (initialOffset == null)
                    stringResource(R.string.create_new_offset)
                else stringResource(R.string.edit_offset),
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OffsetWheelPicker(
                    days = days,
                    hours = hours,
                    minutes = minutes,
                    seconds = seconds,
                    onChange = { d, h, m, s ->
                        days = d
                        hours = h
                        minutes = m
                        seconds = s
                    }
                )

                if (initialOffset != null) {
                    OutlinedButton(
                        onClick = { showConfirmDelete = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = AppObjectsColors.cancelButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.delete_offset),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    scope.launch {
                        val total = hours * 3600 + minutes * 60 + seconds
                        val newOffset = initialOffset?.copy(offset = total)
                            ?: OffsetItem(offset = total)
                        if (initialOffset == null)
                            OffsetsSettingsStore.addOffset(ctx, newOffset)
                        else
                            OffsetsSettingsStore.updateOffset(ctx, newOffset)
                        onDismiss()
                    }
                }
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )

    if (showConfirmDelete && initialOffset != null) {
        UserValidation(
            title = stringResource(R.string.delete_offset),
            message = "${stringResource(R.string.are_you_sure_to_delete_offset)} '${initialOffset.offset}'?",
            onCancel = { showConfirmDelete = false },
            onAgree = {
                showConfirmDelete = false
                scope.launch {
                    OffsetsSettingsStore.deleteOffset(ctx, initialOffset)
                    onDismiss()
                }
            }
        )
    }
}

@Composable
private fun OffsetWheelPicker(
    days: Int,
    hours: Int,
    minutes: Int,
    seconds: Int,
    onChange: (Int, Int, Int, Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        WheelColumn(
            label = "d",
            range = 0..23,
            selected = days,
            onSelected = { onChange(it, hours, minutes, seconds) }
        )
        WheelColumn(
            label = "h",
            range = 0..23,
            selected = hours,
            onSelected = { onChange(days, it, minutes, seconds) }
        )
        WheelColumn(
            label = "m",
            range = 0..59,
            selected = minutes,
            onSelected = { onChange(days, hours, it, seconds) }
        )
        WheelColumn(
            label = "s",
            range = 0..59,
            selected = seconds,
            onSelected = { onChange(days, hours, minutes, it) }
        )
    }
}

@SuppressLint("FrequentlyChangingValue")
@Composable
private fun WheelColumn(
    label: String,
    range: IntRange,
    selected: Int,
    onSelected: (Int) -> Unit
) {
    val values = range.toList()
    val count = values.size
    val visibleItems = 5
    val itemHeight = 40.dp
    val totalVisibleHeight = itemHeight * visibleItems

    val paddingItems = visibleItems / 2

    // Start in the middle of the virtual infinite list
    val infiniteCount = count * 1000
    val startIndex = (infiniteCount / 2) - ((infiniteCount / 2) % count) + selected

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = startIndex - paddingItems)

    LaunchedEffect(selected) {
        listState.animateScrollToItem(startIndex - paddingItems)
    }

    // Detect selected item
    LaunchedEffect(listState.firstVisibleItemScrollOffset, listState.firstVisibleItemIndex) {
        val centerPosition = listState.firstVisibleItemIndex + paddingItems
        val centeredIndex = centerPosition % count
        val value = values[centeredIndex]
        if (value != selected) onSelected(value)
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .height(totalVisibleHeight)
                .width(70.dp)
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                state = listState,
                horizontalAlignment = Alignment.CenterHorizontally,
                flingBehavior = androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior(listState),
                modifier = Modifier.align(Alignment.Center)
            ) {
                item {
                    Spacer(Modifier.height(itemHeight))
                }

                items(infiniteCount) { index ->
                    val value = values[index % count]
                    val distanceFromCenter =
                        kotlin.math.abs(index - (listState.firstVisibleItemIndex + paddingItems)/* - visibleItems / 2*/)
                    val alpha = 1f - (distanceFromCenter * 0.3f).coerceIn(0f, 1f)

                    Text(
                        text = value.toString().padStart(2, '0'),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = if (value == selected) FontWeight.Bold else FontWeight.Normal,
                            color = if (value == selected)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = alpha)
                        ),
                        modifier = Modifier
                            .height(itemHeight)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                item {
                    Spacer(Modifier.height(itemHeight))
                }
            }

//            // Optional center highlight overlay
//            Box(
//                modifier = Modifier
//                    .align(Alignment.Center)
//                    .padding(horizontal = 5.dp)
//                    .fillMaxWidth()
//                    .height(itemHeight)
//                    .background(
//                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
//                        shape = MaterialTheme.shapes.small
//                    )
//            )
        }
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

