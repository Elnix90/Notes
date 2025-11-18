package org.elnix.notes.ui.helpers.toolbars

import android.content.Context
import androidx.compose.foundation.ScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import org.elnix.notes.data.helpers.ClickType
import org.elnix.notes.data.helpers.GlobalNotesActions
import org.elnix.notes.data.helpers.TagItem
import org.elnix.notes.data.helpers.ToolBars
import org.elnix.notes.data.helpers.defaultToolbarItems
import org.elnix.notes.data.helpers.toolbarName
import org.elnix.notes.data.settings.stores.ToolbarItemsSettingsStore
import org.elnix.notes.data.settings.stores.ToolbarSetting
import org.elnix.notes.ui.theme.adjustBrightness

@Composable
fun UnifiedToolbar(
    ctx: Context,
    toolbar: ToolbarSetting,
    scrollState: ScrollState,
    isMultiSelect: Boolean,
    isSearchExpanded: Boolean,
    ghosted: Boolean = false,
    scale: Float = 1f,
    onSearchChange: ((String) -> Unit)? = null,
    onActionClick: ((GlobalNotesActions, ClickType, TagItem?, ToolBars) -> Unit)? = null
) {
    val toolbarItemsState = remember { ToolbarItemsSettingsStore.getToolbarItemsFlow(ctx, toolbar.toolbar) }
        .collectAsState(initial = defaultToolbarItems(toolbar.toolbar))

    val toolbarItems = toolbarItemsState.value

    val toolbarColor = toolbar.color ?: MaterialTheme.colorScheme.surface
    val toolbarBorderColor = toolbar.borderColor ?: toolbarColor.adjustBrightness(3f)


    ToolbarCard(
        ctx = ctx,
        items = toolbarItems,
        scrollState = scrollState,
        name = toolbarName(toolbar),
        showName = toolbar.showName,
        isMultiSelect = isMultiSelect,
        isSearchExpanded = isSearchExpanded,
        height = 80.dp,
        backgroundColor = toolbarColor,
        borderColor = toolbarBorderColor,
        borderWidth = toolbar.borderWidth,
        borderRadius = toolbar.borderRadius,
        elevation = toolbar.elevation,
        paddingLeft = toolbar.leftPadding,
        paddingRight = toolbar.rightPadding,
        ghosted = ghosted,
        scale = scale,
        onSearchChange = { onSearchChange?.invoke(it) }
    ) { action, clickType, tagItem ->
        onActionClick?.invoke(action, clickType, tagItem, toolbar.toolbar)
    }
}