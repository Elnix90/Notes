package org.elnix.notes.data.helpers

enum class ToolBars {
    SELECT,
    SEPARATOR,
    TAGS,
    QUICK_ACTIONS
}

fun defaultToolbarItems(toolbar: ToolBars): List<GlobalNotesActions> = when (toolbar) {
    ToolBars.SELECT -> listOf(
        GlobalNotesActions.DESELECT_ALL,
        GlobalNotesActions.SPACER1,
        GlobalNotesActions.EDIT_NOTE,
        GlobalNotesActions.COMPLETE_NOTE,
        GlobalNotesActions.DELETE_NOTE
    )
    ToolBars.SEPARATOR -> emptyList()
    ToolBars.TAGS -> emptyList()
    ToolBars.QUICK_ACTIONS -> listOf(
        GlobalNotesActions.SEARCH,
        GlobalNotesActions.SPACER1,
        GlobalNotesActions.ADD_NOTE,
        GlobalNotesActions.SPACER2,
        GlobalNotesActions.SORT,
        GlobalNotesActions.REORDER,
        GlobalNotesActions.SETTINGS
    )
}

