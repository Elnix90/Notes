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
import org.elnix.notes.data.ActionSettings
import org.elnix.notes.data.SettingsStore
import org.elnix.notes.ui.helpers.ActionSelectorRow
import org.elnix.notes.ui.helpers.ColorPickerRow
import org.elnix.notes.ui.helpers.SettingsTitle
import org.elnix.notes.ui.theme.AppObjectsColors

@Composable
fun CustomisationTab(ctx: Context, scope: CoroutineScope, onBack: (() -> Unit)) {
    val settings by SettingsStore.getActionSettingsFlow(ctx).collectAsState(initial = ActionSettings())

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SettingsTitle("Customisation", onBack)

        // --- Swipe Left ---
        ActionSelectorRow(
            label = "Swipe Left Action",
            selected = settings.leftAction,
            onSelected = { scope.launch { SettingsStore.setSwipeLeftAction(ctx, it) } }
        )

        // --- Swipe Right ---
        ActionSelectorRow(
            label = "Swipe Right Action",
            selected = settings.rightAction,
            onSelected = { scope.launch { SettingsStore.setSwipeRightAction(ctx, it) } }
        )

        // --- Click Action ---
        ActionSelectorRow(
            label = "Click Action",
            selected = settings.clickAction,
            onSelected = { scope.launch { SettingsStore.setClickAction(ctx, it) } }
        )
    }
}
