package org.elnix.notes.ui.settings

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.elnix.notes.data.settings.ActionSettings
import org.elnix.notes.data.settings.ActionSettingsStore.getActionSettingsFlow
import org.elnix.notes.data.settings.ActionSettingsStore.setClickAction
import org.elnix.notes.data.settings.ActionSettingsStore.setSwipeLeftAction
import org.elnix.notes.data.settings.ActionSettingsStore.setSwipeRightAction
import org.elnix.notes.ui.helpers.ActionSelectorRow
import org.elnix.notes.ui.helpers.SettingsTitle

@Composable
fun CustomisationTab(ctx: Context, scope: CoroutineScope, onBack: (() -> Unit)) {
    val settings by getActionSettingsFlow(ctx).collectAsState(initial = ActionSettings())

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
            onSelected = { scope.launch { setSwipeLeftAction(ctx, it) } }
        )

        // --- Swipe Right ---
        ActionSelectorRow(
            label = "Swipe Right Action",
            selected = settings.rightAction,
            onSelected = { scope.launch { setSwipeRightAction(ctx, it) } }
        )

        // --- Click Action ---
        ActionSelectorRow(
            label = "Click Action",
            selected = settings.clickAction,
            onSelected = { scope.launch { setClickAction(ctx, it) } }
        )
    }
}
