package org.elnix.notes.data.helpers

enum class ToolBars {
    SELECT,
    SEPARATOR,
    TAGS,
    QUICK_ACTIONS
}

fun defaultToolbarItems(toolbar: ToolBars): List<GlobalNotesActions> = when (toolbar) {
    ToolBars.SELECT -> listOf(GlobalNotesActions.DESELECT_ALL, GlobalNotesActions.REORDER)
    ToolBars.SEPARATOR -> emptyList()
    ToolBars.TAGS -> listOf(GlobalNotesActions.SEARCH, GlobalNotesActions.SORT)
    ToolBars.QUICK_ACTIONS -> listOf(GlobalNotesActions.ADD_NOTE, GlobalNotesActions.EDIT_NOTE, GlobalNotesActions.DELETE_NOTE, GlobalNotesActions.COMPLETE_NOTE)
}

