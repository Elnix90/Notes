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
fun TagsToolbar(
    ctx: Context,
    scrollState: ScrollState,
    color: Color = MaterialTheme.colorScheme.surface,
    ghosted: Boolean = false,
    scale: Float = 1f,
    onActionCLick: (GlobalNotesActions) -> Unit
) {
    val tagsToolbarItemsState = remember { ToolbarItemsSettingsStore.getToolbarItemsFlow(ctx, ToolBars.TAGS) }
        .collectAsState(initial = defaultToolbarItems(ToolBars.TAGS))

    val tagsToolbarItems = tagsToolbarItemsState.value


    ToolbarCard(
        ctx = ctx,
        actions = tagsToolbarItems,
        scrollState = scrollState,
        color = color,
        ghosted = ghosted,
        scale = scale
    ) { onActionCLick(it) }
}