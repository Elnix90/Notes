package org.elnix.notes.ui.settings.customisation

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Route
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.elnix.notes.R
import org.elnix.notes.Routes
import org.elnix.notes.data.helpers.NoteActionSettings
import org.elnix.notes.data.helpers.NotesActions
import org.elnix.notes.data.helpers.noteActionName
import org.elnix.notes.data.settings.stores.ActionSettingsStore
import org.elnix.notes.data.settings.stores.ToolbarsSettingsStore
import org.elnix.notes.data.settings.stores.UiSettingsStore
import org.elnix.notes.ui.helpers.ActionSelectorRow
import org.elnix.notes.ui.helpers.SwitchRow
import org.elnix.notes.ui.helpers.TextDivider
import org.elnix.notes.ui.helpers.settings.SettingsItem
import org.elnix.notes.ui.helpers.toolbars.SliderToolbarSetting
import org.elnix.notes.ui.settings.SettingsLazyHeader

@Composable
fun CustomisationTab(
    ctx: Context,
    scope: CoroutineScope,
    navController: NavController,
    onBack: (() -> Unit)
) {
    val settings by ActionSettingsStore.getActionSettingsFlow(ctx).collectAsState(initial = NoteActionSettings())


    val showDeleteButton by UiSettingsStore.getShowDeleteButton(ctx).collectAsState(initial = true)
    val showNoteTypeIcon by UiSettingsStore.getShowNoteTypeIcon(ctx).collectAsState(initial = true)

    val showTagsInNotes by UiSettingsStore.getShowTagsInNotes(ctx).collectAsState(initial = true)

    val toolbarsSpacing by ToolbarsSettingsStore.getToolbarsSpacing(ctx).collectAsState(initial = 8)


    SettingsLazyHeader(
        title = stringResource(R.string.customisation),
        onBack = onBack,
        helpText = stringResource(R.string.customisation_text),
        onReset = {
            scope.launch {
                UiSettingsStore.resetAll(ctx)
                ActionSettingsStore.resetAll(ctx)
            }
        }
    ) {

        item { TextDivider(stringResource(R.string.note_actions)) }

        // --- Swipe Left ---
        item {
            ActionSelectorRow(
                label = stringResource(R.string.swipe_left_action),
                options = NotesActions.entries,
                selected = settings.leftAction,
                optionLabel = { noteActionName(ctx, it) }
            ) {
                scope.launch { ActionSettingsStore.setSwipeLeftAction(ctx, it) }
            }
        }

        // --- Swipe Right ---
        item {
            ActionSelectorRow(
                label = stringResource(R.string.swipe_right_action),
                options = NotesActions.entries,
                selected = settings.rightAction,
                optionLabel = { noteActionName(ctx, it) }
            ) {
                scope.launch { ActionSettingsStore.setSwipeRightAction(ctx, it) }
            }
        }

        // --- Click Action ---
        item {
            ActionSelectorRow(
                label = stringResource(R.string.click_action),
                options = NotesActions.entries,
                selected = settings.clickAction,
                optionLabel = { noteActionName(ctx, it) }
            ) {
                scope.launch { ActionSettingsStore.setClickAction(ctx, it) }
            }
        }

        // --- Long Click Action ---
        item {
            ActionSelectorRow(
                label = stringResource(R.string.long_click_action),
                options = NotesActions.entries,
                selected = settings.longClickAction,
                optionLabel = { noteActionName(ctx, it) }
            ) {
                scope.launch { ActionSettingsStore.setLongClickAction(ctx, it) }
            }
        }

        // --- Type Button action ---
        item {
            ActionSelectorRow(
                label = stringResource(R.string.type_button_action),
                options = NotesActions.entries,
                selected = settings.typeButtonAction,
                enabled = showNoteTypeIcon,
                optionLabel = { noteActionName(ctx, it) }
            ) {
                scope.launch { ActionSettingsStore.setTypeButtonAction(ctx, it) }
            }
        }

        item { TextDivider(stringResource(R.string.buttons_display)) }


        item {
            SwitchRow(
                showNoteTypeIcon,
                stringResource(R.string.show_note_type_icon),
            ) {
                scope.launch { UiSettingsStore.setShowNoteTypeIcon(ctx, it) }
            }
        }

        item {
            SwitchRow(
                showDeleteButton,
                stringResource(R.string.show_delete_button),
            ) {
                scope.launch { UiSettingsStore.setShowDeleteButton(ctx, it) }
            }
        }

        // Tags Category
        item { TextDivider(stringResource(R.string.tags)) }

        item {
            SwitchRow(
                showTagsInNotes,
                stringResource(R.string.show_tags_in_notes),
            ) {
                scope.launch { UiSettingsStore.setShowTagsInNotes(ctx, it) }
            }
        }

        item { TextDivider(stringResource(R.string.toolbars)) }

        item {
            SettingsItem(
                title = stringResource(R.string.toolbars),
                icon = Icons.Default.Route
            ) {
                navController.navigate(Routes.Settings.CustomisationSub.TOOLBARS)
            }
        }

        item {
            // Toolbars spacing
            SliderToolbarSetting(
                label = { value ->
                    "${stringResource(R.string.toolbars_spacing)}: $value px"
                },
                initialValue = toolbarsSpacing,
                valueRange = 0f..100.toFloat(),
                steps = 99,
                onReset = { scope.launch { ToolbarsSettingsStore.setToolbarsSpacing(ctx, 8) } },
                onValueChangeFinished = { v ->
                    scope.launch { ToolbarsSettingsStore.setToolbarsSpacing(ctx, v) }
                }
            )
        }

    }
}
