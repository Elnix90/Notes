package org.elnix.notes.ui.helpers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.notes.R
import org.elnix.notes.data.helpers.SortMode
import org.elnix.notes.data.helpers.SortType
import org.elnix.notes.data.helpers.sortModeIcon
import org.elnix.notes.data.helpers.sortModeName
import org.elnix.notes.data.helpers.sortTypeIcon
import org.elnix.notes.data.helpers.sortTypeName
import org.elnix.notes.data.settings.stores.SortSettingsStore
import org.elnix.notes.ui.theme.AppObjectsColors
import org.elnix.notes.ui.theme.adjustBrightness

@Composable
fun SortSelectorDialog(
    surfaceColor: Color = MaterialTheme.colorScheme.surface,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    onClose: () -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val currentSortMode by SortSettingsStore.getSortMode(ctx).collectAsState(initial = SortMode.DESC)
    val currentSortType by SortSettingsStore.getSortType(ctx).collectAsState(initial = SortType.CUSTOM)

    AlertDialog(
        onDismissRequest = { onClose() },
        shape = RoundedCornerShape(16.dp),
        containerColor = surfaceColor,
        title = {
            Text(
                text = stringResource(R.string.select_a_sort_option),
                style = MaterialTheme.typography.titleMedium,
                color = textColor
            )
        },

        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                TextDivider(
                    text = stringResource(R.string.sort_mode),
                    modifier = Modifier.padding(horizontal = 30.dp),
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    thickness = 5.dp
                )

                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface.adjustBrightness(0.7f))
                ) {
                    SortType.entries.forEach { type ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    scope.launch {
                                        SortSettingsStore.setSortType(ctx, type)
                                        onClose()
                                    }
                                }
                                .padding(horizontal = 4.dp)
                        ) {
                            RadioButton(
                                selected = (currentSortType == type),
                                onClick = {
                                    scope.launch {
                                        SortSettingsStore.setSortType(ctx, type)
                                        onClose()
                                    }
                                },
                                colors = AppObjectsColors.radioButtonColors()
                            )

                            Text(
                                text = sortTypeName(ctx, type),
                                color = textColor,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Spacer(Modifier.weight(1f))

                            Icon(
                                imageVector = sortTypeIcon(type),
                                contentDescription = sortTypeName(ctx, type),
                                tint = MaterialTheme.colorScheme.outline.copy(0.7f),
                                modifier = Modifier.padding(end = 5.dp)
                            )
                        }
                    }
                }

                TextDivider(
                    text = stringResource(R.string.sort_type),
                    modifier = Modifier.padding(horizontal = 30.dp),
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    thickness = 5.dp
                )

                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface.adjustBrightness(0.7f))
                ) {
                    SortMode.entries.forEach { mode ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    scope.launch {
                                        SortSettingsStore.setSortMode(ctx, mode)
                                    }
                                }
                                .padding(horizontal = 4.dp)
                        ) {
                            RadioButton(
                                selected = (currentSortMode == mode),
                                onClick = {
                                    scope.launch {
                                        SortSettingsStore.setSortMode(ctx, mode)
                                    }
                                },
                                colors = AppObjectsColors.radioButtonColors()
                            )

                            Text(
                                text = sortModeName(ctx, mode),
                                color = textColor,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Spacer(Modifier.weight(1f))

                            Icon(
                                imageVector = sortModeIcon(mode),
                                contentDescription = sortModeName(ctx, mode),
                                tint = MaterialTheme.colorScheme.outline.copy(0.7f),
                                modifier = Modifier.padding(end = 5.dp)
                            )
                        }
                    }
                }
            }
        },

        dismissButton = {
            Text(
                text = stringResource(R.string.ok),
                color = textColor,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { onClose() }
            )
        },

        confirmButton = {}
    )
}
