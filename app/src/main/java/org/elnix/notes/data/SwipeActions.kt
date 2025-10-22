package org.elnix.notes.data

enum class Action { DELETE, COMPLETE, EDIT }

data class ActionSettings(
    val leftAction: Action = Action.DELETE,
    val rightAction: Action = Action.EDIT,
    val clickAction: Action = Action.COMPLETE
)
