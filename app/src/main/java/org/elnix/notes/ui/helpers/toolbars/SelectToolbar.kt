package org.elnix.notes.ui.helpers.toolbars

import android.content.Context
import androidx.compose.foundation.ScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import org.elnix.notes.data.helpers.GlobalNotesActions
import org.elnix.notes.data.helpers.ToolBars
import org.elnix.notes.data.helpers.defaultToolbarItems
import org.elnix.notes.data.settings.stores.ToolbarItemsSettingsStore

@Composable
fun SelectToolbar(
    ctx: Context,
    scrollState: ScrollState,
    color: Color = MaterialTheme.colorScheme.surface,
    ghosted: Boolean = false,
    scale: Float = 1f,
    onActionCLick: (GlobalNotesActions) -> Unit
) {
    val selectToolbarItemsState = remember { ToolbarItemsSettingsStore.getToolbarItemsFlow(ctx, ToolBars.SELECT) }
        .collectAsState(initial = defaultToolbarItems(ToolBars.SELECT))

    val selectToolbarItems = selectToolbarItemsState.value


    ToolbarCard(
        ctx = ctx,
        actions = selectToolbarItems,
        scrollState = scrollState,
        color = color,
        ghosted = ghosted,
        scale = scale
    ) { onActionCLick(it) }
}