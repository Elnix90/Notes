package org.elnix.notes.data.helpers

import org.elnix.notes.data.settings.stores.ToolbarItemState

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
        GlobalNotesActions.EDIT_NOTE,
        GlobalNotesActions.COMPLETE_NOTE,
        GlobalNotesActions.DELETE_NOTE
    )
    ToolBars.QUICK_ACTIONS -> listOf(
        GlobalNotesActions.REORDER,
//        GlobalNotesActions.SEARCH,
//        GlobalNotesActions.SPACER1,
        GlobalNotesActions.ADD_NOTE,
        GlobalNotesActions.SPACER2,
//        GlobalNotesActions.SORT,
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
    ToolBars.QUICK_ACTIONS -> listOf(GlobalNotesActions.SEARCH)
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

