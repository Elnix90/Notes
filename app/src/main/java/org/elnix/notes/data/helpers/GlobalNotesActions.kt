package org.elnix.notes.data.helpers

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Reorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import org.elnix.notes.R
import org.elnix.notes.ui.theme.LocalExtraColors

enum class GlobalNotesActions {
    SEARCH, SORT, SETTINGS, DESELECT_ALL, ADD_NOTE, REORDER, EDIT_NOTE, DELETE_NOTE, COMPLETE_NOTE
}

@Composable
fun globalActionIcon(action: GlobalNotesActions): ImageVector = when (action) {
    GlobalNotesActions.SEARCH -> Icons.Default.Search
    GlobalNotesActions.SORT -> Icons.AutoMirrored.Filled.Sort
    GlobalNotesActions.SETTINGS -> Icons.Default.Settings
    GlobalNotesActions.DESELECT_ALL -> Icons.Default.Close
    GlobalNotesActions.ADD_NOTE -> Icons.Default.Add
    GlobalNotesActions.REORDER -> Icons.Default.Reorder
    GlobalNotesActions.EDIT_NOTE -> Icons.Default.Edit
    GlobalNotesActions.DELETE_NOTE -> Icons.Default.Delete
    GlobalNotesActions.COMPLETE_NOTE -> Icons.Default.CheckCircle
}

@Composable
fun globalActionColor(action: GlobalNotesActions): Color {
    val extras = LocalExtraColors.current
    return when (action) {
        GlobalNotesActions.SEARCH -> extras.edit
        GlobalNotesActions.SORT -> extras.complete
        GlobalNotesActions.SETTINGS -> extras.select
        GlobalNotesActions.DESELECT_ALL -> extras.delete
        GlobalNotesActions.ADD_NOTE -> extras.complete
        GlobalNotesActions.REORDER -> extras.edit
        GlobalNotesActions.EDIT_NOTE -> extras.edit
        GlobalNotesActions.DELETE_NOTE -> extras.delete
        GlobalNotesActions.COMPLETE_NOTE -> extras.complete
    }
}

fun globalActionName(ctx: Context, action: GlobalNotesActions): String = when (action) {
    GlobalNotesActions.SEARCH -> ctx.getString(R.string.search)
    GlobalNotesActions.SORT -> ctx.getString(R.string.sort)
    GlobalNotesActions.SETTINGS -> ctx.getString(R.string.settings)
    GlobalNotesActions.DESELECT_ALL -> ctx.getString(R.string.deselect_all)
    GlobalNotesActions.ADD_NOTE -> ctx.getString(R.string.add_note)
    GlobalNotesActions.REORDER -> ctx.getString(R.string.reorder)
    GlobalNotesActions.EDIT_NOTE -> ctx.getString(R.string.edit)
    GlobalNotesActions.DELETE_NOTE -> ctx.getString(R.string.delete)
    GlobalNotesActions.COMPLETE_NOTE -> ctx.getString(R.string.complete)
}
