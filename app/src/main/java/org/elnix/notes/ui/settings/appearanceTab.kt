package org.elnix.notes.ui.settings

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.elnix.notes.data.SettingsStore
import org.elnix.notes.ui.helpers.ColorPickerRow
import org.elnix.notes.ui.helpers.SettingsTitle
import org.elnix.notes.ui.theme.AppObjectsColors

@Composable
fun AppearanceTab(ctx: Context, scope: CoroutineScope, onBack: (() -> Unit)) {
    val primary by SettingsStore.getPrimaryFlow(ctx).collectAsState(initial = null)
    val background by SettingsStore.getBackgroundFlow(ctx).collectAsState(initial = null)
    val onBackground by SettingsStore.getOnBackgroundFlow(ctx).collectAsState(initial = null)

    val showNavbarLabels by SettingsStore.getShowBottomNavLabelsFlow(ctx).collectAsState(initial = true)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SettingsTitle("Appearance", onBack)

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ColorPickerRow(
                label = "Primary",
                currentColor = primary ?: MaterialTheme.colorScheme.primary.toArgb()
            ) {
                scope.launch { SettingsStore.setPrimary(ctx, it) }
            }

            ColorPickerRow(
                label = "Background",
                currentColor = background ?: MaterialTheme.colorScheme.background.toArgb()
            ) {
                scope.launch { SettingsStore.setBackground(ctx, it) }
            }

            ColorPickerRow(
                label = "Text",
                currentColor = onBackground ?: MaterialTheme.colorScheme.onBackground.toArgb()
            ) {
                scope.launch { SettingsStore.setOnBackground(ctx, it) }
            }

            Button(
                onClick = { scope.launch { SettingsStore.resetColors(ctx) } },
                modifier = Modifier.fillMaxWidth(),
                colors = AppObjectsColors.buttonColors()
            ) {
                Text("Reset to Default Colors")
            }

            HorizontalDivider()

            var checked by remember { mutableStateOf(showNavbarLabels ?: true) }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        checked = !checked
                        scope.launch { SettingsStore.setShowBottomNavLabelsFlow(ctx, checked) }
                    }
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text= "Show Navigation Bar Labels",
                    color = MaterialTheme.colorScheme.onBackground
                )
                Switch(
                    checked = showNavbarLabels ?: true,
                    onCheckedChange = { scope.launch { SettingsStore.setShowBottomNavLabelsFlow(ctx, it) } },
                    colors = AppObjectsColors.switchColors()
                )
            }
        }
    }
}
