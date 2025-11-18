package org.elnix.notes.data.helpers

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import org.elnix.notes.R
import org.elnix.notes.data.settings.stores.ToolbarItemState
import org.elnix.notes.data.settings.stores.ToolbarSetting

enum class ToolBars {
    SELECT,
    SEPARATOR,
    TAGS,
    QUICK_ACTIONS
}

fun defaultEnabledItems (toolbar: ToolBars): List<GlobalNotesActions>? = when (toolbar) {
    ToolBars.SELECT -> listOf(
        GlobalNotesActions.DESELECT_ALL,
        GlobalNotesActions.SPACER1,
        GlobalNotesActions.DUPLICATE_NOTE,
        GlobalNotesActions.EDIT_NOTE,
        GlobalNotesActions.COMPLETE_NOTE,
        GlobalNotesActions.DELETE_NOTE
    )
    ToolBars.QUICK_ACTIONS -> listOf(
        GlobalNotesActions.SEARCH,
        GlobalNotesActions.ADD_NOTE,
        GlobalNotesActions.SORT,
        GlobalNotesActions.SPACER2,
        GlobalNotesActions.SETTINGS
    )

    ToolBars.TAGS -> listOf(
        GlobalNotesActions.TAG_FILTER,
        GlobalNotesActions.TAGS,
        GlobalNotesActions.ADD_TAG
    )
    ToolBars.SEPARATOR -> null
}


fun defaultShowLabelItems (toolbar: ToolBars): List<GlobalNotesActions>? = when (toolbar) {
    ToolBars.SELECT -> null
    ToolBars.QUICK_ACTIONS -> listOf(GlobalNotesActions.ADD_NOTE)
    ToolBars.SEPARATOR -> null
    ToolBars.TAGS -> listOf(GlobalNotesActions.ADD_TAG)
}
fun defaultToolbarItems(toolbar: ToolBars): List<ToolbarItemState> {
    val enabledItems = defaultEnabledItems(toolbar)
    val showLabelItems = defaultShowLabelItems(toolbar)
    val unsorted = GlobalNotesActions.entries.map { action ->
        ToolbarItemState(
            action,
            enabledItems?.contains(action) ?: false,
            showLabelItems?.contains(action) ?: false
        )
    }
    // Custom sort:
    return unsorted.sortedWith(compareBy<ToolbarItemState> {
        // Enabled items first
        !it.enabled
    }.thenComparator { a, b ->
        when {
            // If both are enabled, sort within enabled by the order in enabledItems
            a.enabled && b.enabled && enabledItems != null -> {
                enabledItems.indexOf(a.action) - enabledItems.indexOf(b.action)
            }
            else -> 0 // Otherwise, keep current relative order
        }
    })
}

@Composable
fun toolbarName(toolbar: ToolbarSetting) = toolbar.name
    ?: when (toolbar.toolbar) {
        ToolBars.SELECT -> stringResource(R.string.toolbar_select)
        ToolBars.SEPARATOR -> ""
        ToolBars.TAGS -> stringResource(R.string.toolbar_tags)
        ToolBars.QUICK_ACTIONS -> stringResource(R.string.toolbar_quick_actions)
    }

