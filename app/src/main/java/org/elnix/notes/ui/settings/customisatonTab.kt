package org.elnix.notes.ui.settings

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.elnix.notes.R
import org.elnix.notes.data.helpers.NoteActionSettings
import org.elnix.notes.data.helpers.NotesActions
import org.elnix.notes.data.helpers.ToolBars
import org.elnix.notes.data.settings.stores.ActionSettingsStore
import org.elnix.notes.data.settings.stores.UiSettingsStore
import org.elnix.notes.ui.helpers.ActionSelectorRow
import org.elnix.notes.ui.helpers.SwitchRow
import org.elnix.notes.ui.helpers.TextDivider
import org.elnix.notes.ui.helpers.settings.SettingsTitle
import org.elnix.notes.ui.helpers.toolbars.ToolbarItemsEditor
import org.elnix.notes.ui.helpers.toolbars.ToolbarsSettingsRow

@Composable
fun CustomisationTab(ctx: Context, scope: CoroutineScope, onBack: (() -> Unit)) {
    val settings by ActionSettingsStore.getActionSettingsFlow(ctx).collectAsState(initial = NoteActionSettings())


    val showDeleteButton by UiSettingsStore.getShowDeleteButton(ctx).collectAsState(initial = true)
    val showNoteTypeIcon by UiSettingsStore.getShowNoteTypeIcon(ctx).collectAsState(initial = true)

    val showTagSelector by UiSettingsStore.getShowTagSelector(ctx).collectAsState(initial = true)
    val showTagsInNotes by UiSettingsStore.getShowTagsInNotes(ctx).collectAsState(initial = true)


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(
                WindowInsets.systemBars
                    .asPaddingValues()
            )
            .padding(horizontal = 16.dp, vertical = 5.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SettingsTitle(title = stringResource(R.string.customisation), onBack = onBack)

        TextDivider(stringResource(R.string.note_actions))

        // --- Swipe Left ---
        ActionSelectorRow(
            label = stringResource(R.string.swipe_left_action),
            options = NotesActions.entries,
            selected = settings.leftAction,
            optionLabel = { it.name}
        ) {
            scope.launch { ActionSettingsStore.setSwipeLeftAction(ctx, it) }
        }

        // --- Swipe Right ---
        ActionSelectorRow(
            label = stringResource(R.string.swipe_right_action),
            options = NotesActions.entries,
            selected = settings.rightAction,
            optionLabel = { it.name}
        ) {
            scope.launch { ActionSettingsStore.setSwipeRightAction(ctx, it) }
        }

        // --- Click Action ---
        ActionSelectorRow(
            label = stringResource(R.string.click_action),
            options = NotesActions.entries,
            selected = settings.clickAction,
            optionLabel = { it.name}
        ) {
            scope.launch { ActionSettingsStore.setClickAction(ctx, it) }
        }

        // --- Long Click Action ---
        ActionSelectorRow(
            label = stringResource(R.string.long_click_action),
            options = NotesActions.entries,
            selected = settings.longClickAction,
            optionLabel = { it.name}
        ) {
            scope.launch { ActionSettingsStore.setLongClickAction(ctx, it) }
        }

        // --- Type Button action ---
        ActionSelectorRow(
            label = stringResource(R.string.type_button_action),
            options = NotesActions.entries,
            selected = settings.typeButtonAction,
            enabled = showNoteTypeIcon,
            optionLabel = { it.name}
        ) {
            scope.launch { ActionSettingsStore.setTypeButtonAction(ctx, it) }
        }

        TextDivider(stringResource(R.string.buttons_display))


        SwitchRow(
            showNoteTypeIcon,
            stringResource(R.string.show_note_type_icon),
        ) {
            scope.launch { UiSettingsStore.setShowNoteTypeIcon(ctx, it) }
        }

        SwitchRow(
            showDeleteButton,
            stringResource(R.string.show_delete_button),
        ) {
            scope.launch { UiSettingsStore.setShowDeleteButton(ctx, it) }
        }

        // New Tags Category
        TextDivider(stringResource(R.string.tags))

        SwitchRow(
            showTagSelector,
            stringResource(R.string.show_tag_selector),
        ) {
            scope.launch { UiSettingsStore.setShowTagSelector(ctx, it) }
        }

        SwitchRow(
            showTagsInNotes,
            stringResource(R.string.show_tags_in_notes),
        ) {
            scope.launch { UiSettingsStore.setShowTagsInNotes(ctx, it) }
        }

        TextDivider(stringResource(R.string.toolbars))

        ToolbarsSettingsRow(ctx)

        ToolbarItemsEditor(ctx, toolbar = ToolBars.SELECT)

        ToolbarItemsEditor(ctx, toolbar = ToolBars.TAGS)

        ToolbarItemsEditor(ctx, toolbar = ToolBars.QUICK_ACTIONS)
    }
}
