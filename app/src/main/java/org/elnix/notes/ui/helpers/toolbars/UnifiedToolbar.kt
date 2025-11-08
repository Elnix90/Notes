package org.elnix.notes.ui.helpers.toolbars

import android.content.Context
import androidx.compose.foundation.ScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.elnix.notes.data.helpers.ClickType
import org.elnix.notes.data.helpers.GlobalNotesActions
import org.elnix.notes.data.helpers.TagItem
import org.elnix.notes.data.helpers.ToolBars
import org.elnix.notes.data.helpers.defaultToolbarItems
import org.elnix.notes.data.settings.stores.ToolbarItemsSettingsStore

@Composable
fun UnifiedToolbar(
    ctx: Context,
    toolBars: ToolBars,
    scrollState: ScrollState,
    isSearchExpanded: Boolean,
    color: Color = MaterialTheme.colorScheme.surface,
    ghosted: Boolean = false,
    scale: Float = 1f,
    floatingToolbar: Boolean,
    onSearchChange: ((String) -> Unit)? = null,
    onActionClick: (GlobalNotesActions, ClickType, TagItem?, ToolBars) -> Unit
) {
    val toolbarItemsState = remember { ToolbarItemsSettingsStore.getToolbarItemsFlow(ctx, toolBars) }
        .collectAsState(initial = defaultToolbarItems(toolBars))

    val toolbarItems = toolbarItemsState.value


    ToolbarCard(
        ctx = ctx,
        items = toolbarItems,
        scrollState = scrollState,
        isSearchExpanded = isSearchExpanded,
        height = 75.dp,
        color = color,
        ghosted = ghosted,
        scale = scale,
        floatingToolbar = floatingToolbar,
        onSearchChange = { onSearchChange?.invoke(it) }
    ) { action, clickType, tagItem ->
        onActionClick(action, clickType, tagItem, toolBars)
    }
}