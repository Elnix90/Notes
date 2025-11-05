package org.elnix.notes.ui.helpers.toolbars

import android.content.Context
import androidx.compose.foundation.ScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import org.elnix.notes.data.helpers.ClickType
import org.elnix.notes.data.helpers.GlobalNotesActions
import org.elnix.notes.data.helpers.TagItem
import org.elnix.notes.data.helpers.ToolBars
import org.elnix.notes.data.helpers.defaultToolbarItems
import org.elnix.notes.data.settings.stores.ToolbarItemsSettingsStore

@Composable
fun QuickActionsToolbar(
    ctx: Context,
    scrollState: ScrollState,
    color: Color = MaterialTheme.colorScheme.surface,
    ghosted: Boolean = false,
    scale: Float = 1f,
    onActionClick: (GlobalNotesActions, ClickType, TagItem?) -> Unit
) {
    val selectToolbarItemsState = remember { ToolbarItemsSettingsStore.getToolbarItemsFlow(ctx, ToolBars.QUICK_ACTIONS) }
        .collectAsState(initial = defaultToolbarItems(ToolBars.QUICK_ACTIONS))

    val quickToolbarItems = selectToolbarItemsState.value


    ToolbarCard(
        ctx = ctx,
        items = quickToolbarItems,
        scrollState = scrollState,
        color = color,
        ghosted = ghosted,
        scale = scale
    ) { action, clickType, tagItem ->
        onActionClick(action, clickType, tagItem)
    }
}