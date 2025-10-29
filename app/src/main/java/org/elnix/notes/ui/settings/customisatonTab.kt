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
import org.elnix.notes.data.settings.SwipeActionSettings
import org.elnix.notes.data.settings.SwipeActions
import org.elnix.notes.data.settings.stores.ActionSettingsStore.getActionSettingsFlow
import org.elnix.notes.data.settings.stores.ActionSettingsStore.setClickAction
import org.elnix.notes.data.settings.stores.ActionSettingsStore.setSwipeLeftAction
import org.elnix.notes.data.settings.stores.ActionSettingsStore.setSwipeRightAction
import org.elnix.notes.data.settings.stores.UiSettingsStore.getShowNotesNumber
import org.elnix.notes.data.settings.stores.UiSettingsStore.setShowNotesNumber
import org.elnix.notes.ui.helpers.ActionSelectorRow
import org.elnix.notes.ui.helpers.SettingsTitle
import org.elnix.notes.ui.helpers.SwitchRow

@Composable
fun CustomisationTab(ctx: Context, scope: CoroutineScope, onBack: (() -> Unit)) {
    val settings by getActionSettingsFlow(ctx).collectAsState(initial = SwipeActionSettings())

    val showNotesNumber by getShowNotesNumber(ctx).collectAsState(initial = true)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SettingsTitle(title = "Customisation", onBack = onBack)

        // --- Swipe Left ---
        ActionSelectorRow(
            label = "Swipe Left Action",
            options = SwipeActions.entries,
            selected = settings.leftAction,
            optionLabel = { it.name}
        ) {
            scope.launch { setSwipeLeftAction(ctx, it) }
        }

        // --- Swipe Right ---
        ActionSelectorRow(
            label = "Swipe Right Action",
            options = SwipeActions.entries,
            selected = settings.rightAction,
            optionLabel = { it.name}
        ) {
            scope.launch { setSwipeRightAction(ctx, it) }
        }

        // --- Click Action ---
        ActionSelectorRow(
            label = "Click Action",
            options = SwipeActions.entries,
            selected = settings.clickAction,
            optionLabel = { it.name}
        ) {
            scope.launch { setClickAction(ctx, it) }
        }

        SwitchRow(
            showNotesNumber,
            "Show Notes Number",
        ) {
            scope.launch { setShowNotesNumber(ctx, it) }
        }
    }
}
