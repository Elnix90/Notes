package org.elnix.notes.ui.settings

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.elnix.notes.R
import org.elnix.notes.data.helpers.NoteViewType
import org.elnix.notes.data.settings.NoteActionSettings
import org.elnix.notes.data.settings.NotesActions
import org.elnix.notes.data.settings.stores.ActionSettingsStore.getActionSettingsFlow
import org.elnix.notes.data.settings.stores.ActionSettingsStore.setClickAction
import org.elnix.notes.data.settings.stores.ActionSettingsStore.setSwipeLeftAction
import org.elnix.notes.data.settings.stores.ActionSettingsStore.setSwipeRightAction
import org.elnix.notes.data.settings.stores.UiSettingsStore.getNoteViewType
import org.elnix.notes.data.settings.stores.UiSettingsStore.getShowDeleteButton
import org.elnix.notes.data.settings.stores.UiSettingsStore.getShowNotesNumber
import org.elnix.notes.data.settings.stores.UiSettingsStore.setNoteViewType
import org.elnix.notes.data.settings.stores.UiSettingsStore.setShowDeleteButton
import org.elnix.notes.data.settings.stores.UiSettingsStore.setShowNotesNumber
import org.elnix.notes.ui.helpers.ActionSelectorRow
import org.elnix.notes.ui.helpers.SettingsTitle
import org.elnix.notes.ui.helpers.SwitchRow

@Composable
fun CustomisationTab(ctx: Context, scope: CoroutineScope, onBack: (() -> Unit)) {
    val settings by getActionSettingsFlow(ctx).collectAsState(initial = NoteActionSettings())

    val showNotesNumber by getShowNotesNumber(ctx).collectAsState(initial = true)
    val showDeleteButton by getShowDeleteButton(ctx).collectAsState(initial = true)
    val notesViewType by getNoteViewType(ctx).collectAsState(initial = NoteViewType.LIST)


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SettingsTitle(title = stringResource(R.string.customisation), onBack = onBack)

        // --- Swipe Left ---
        ActionSelectorRow(
            label = stringResource(R.string.swipe_left_action),
            options = NotesActions.entries,
            selected = settings.leftAction,
            optionLabel = { it.name}
        ) {
            scope.launch { setSwipeLeftAction(ctx, it) }
        }

        // --- Swipe Right ---
        ActionSelectorRow(
            label = stringResource(R.string.swipe_right_action),
            options = NotesActions.entries,
            selected = settings.rightAction,
            optionLabel = { it.name}
        ) {
            scope.launch { setSwipeRightAction(ctx, it) }
        }

        // --- Click Action ---
        ActionSelectorRow(
            label = stringResource(R.string.click_action),
            options = NotesActions.entries,
            selected = settings.clickAction,
            optionLabel = { it.name}
        ) {
            scope.launch { setClickAction(ctx, it) }
        }

        SwitchRow(
            showNotesNumber,
            stringResource(R.string.show_notes_number),
        ) {
            scope.launch { setShowNotesNumber(ctx, it) }
        }

        SwitchRow(
            showDeleteButton,
            stringResource(R.string.show_delete_button),
        ) {
            scope.launch { setShowDeleteButton(ctx, it) }
        }

        ActionSelectorRow(
            label = stringResource(R.string.notes_view_type),
            options = NoteViewType.entries,
            selected = notesViewType,
            optionLabel = { it.name}
        ) {
            scope.launch { setNoteViewType(ctx, it) }
        }
    }
}
