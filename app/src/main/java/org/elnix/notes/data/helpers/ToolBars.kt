package org.elnix.notes.data.helpers

import org.elnix.notes.data.settings.stores.ToolbarItemState

enum class ToolBars {
    SELECT,
    SEPARATOR,
    TAGS,
    QUICK_ACTIONS
}

fun defaultEnabledItems (toolbar: ToolBars): List<GlobalNotesActions> = when (toolbar) {
    ToolBars.SELECT -> listOf(
        GlobalNotesActions.DESELECT_ALL,
        GlobalNotesActions.SPACER1,
        GlobalNotesActions.EDIT_NOTE,
        GlobalNotesActions.COMPLETE_NOTE,
        GlobalNotesActions.DELETE_NOTE
    )
    ToolBars.QUICK_ACTIONS -> listOf(
        GlobalNotesActions.SEARCH,
        GlobalNotesActions.SPACER1,
        GlobalNotesActions.ADD_NOTE,
        GlobalNotesActions.SPACER2,
        GlobalNotesActions.SORT,
        GlobalNotesActions.REORDER,
        GlobalNotesActions.SETTINGS
    )
    ToolBars.SEPARATOR -> emptyList()
    ToolBars.TAGS -> emptyList()
}


fun defaultShowLabelItems (toolbar: ToolBars): List<GlobalNotesActions> = when (toolbar) {
    ToolBars.SELECT -> emptyList()
    ToolBars.QUICK_ACTIONS -> listOf(GlobalNotesActions.SEARCH)
    ToolBars.SEPARATOR -> emptyList()
    ToolBars.TAGS -> emptyList()
}
fun defaultToolbarItems(toolbar: ToolBars): List<ToolbarItemState> {
    val enabledItems = defaultEnabledItems(toolbar)
    val showLabelItems = defaultShowLabelItems(toolbar)
    return GlobalNotesActions.entries.map { action ->
        ToolbarItemState(action, enabledItems.contains(action), showLabelItems.contains(action))
    }
}
