package org.elnix.notes.ui.settings.customisation

import android.content.Context
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Route
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.elnix.notes.R
import org.elnix.notes.Routes
import org.elnix.notes.data.helpers.GlobalNotesActions
import org.elnix.notes.data.helpers.NoteActionSettings
import org.elnix.notes.data.helpers.NotesActions
import org.elnix.notes.data.helpers.ToolBars
import org.elnix.notes.data.helpers.defaultToolbarItems
import org.elnix.notes.data.helpers.noteActionName
import org.elnix.notes.data.settings.stores.ActionSettingsStore
import org.elnix.notes.data.settings.stores.ToolbarItemsSettingsStore
import org.elnix.notes.data.settings.stores.ToolbarsSettingsStore
import org.elnix.notes.data.settings.stores.UiSettingsStore
import org.elnix.notes.ui.helpers.ActionSelectorRow
import org.elnix.notes.ui.helpers.SwitchRow
import org.elnix.notes.ui.helpers.TextDivider
import org.elnix.notes.ui.helpers.UserValidation
import org.elnix.notes.ui.helpers.settings.SettingsItem
import org.elnix.notes.ui.helpers.settings.SettingsLazyHeader
import org.elnix.notes.ui.helpers.toolbars.SliderToolbarSetting

@Composable
fun CustomisationTab(
    ctx: Context,
    scope: CoroutineScope,
    navController: NavController,
    onBack: (() -> Unit)
) {
    val settings by ActionSettingsStore.getActionSettingsFlow(ctx)
        .collectAsState(initial = NoteActionSettings())
    val showTagsInNotes by UiSettingsStore.getShowTagsInNotes(ctx)
        .collectAsState(initial = true)
    val toolbarsSpacing by ToolbarsSettingsStore.getToolbarsSpacing(ctx)
        .collectAsState(initial = 8)

    val sourceList by remember { ToolbarsSettingsStore.getToolbarsFlow(ctx) }
        .collectAsState(initial = ToolbarsSettingsStore.defaultList)
    val showBottomSettings by UiSettingsStore.getShowBottomDeleteButton(ctx)
        .collectAsState(initial = false)

    val quickActionsItemsState = remember { ToolbarItemsSettingsStore.getToolbarItemsFlow(ctx, ToolBars.QUICK_ACTIONS) }
        .collectAsState(initial = defaultToolbarItems(ToolBars.QUICK_ACTIONS))

    val quickActionsItems = quickActionsItemsState.value

    val actionsEntries = NotesActions.entries.filter { it != NotesActions.NONE }

    var showDangerAboutUnabilityToAccessSettings by remember { mutableStateOf(false) }

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

        // Unified item for all six actions
        item {
            ActionsItems(
                ctx = ctx,
                scope = scope,
                settings = settings,
                actionsEntries = actionsEntries
            )
        }

        item { TextDivider(stringResource(R.string.tags)) }

        item {
            SwitchRow(
                showTagsInNotes,
                stringResource(R.string.show_tags_in_notes),
            ) { scope.launch { UiSettingsStore.setShowTagsInNotes(ctx, it) } }
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
            SliderToolbarSetting(
                label = { v -> "${stringResource(R.string.toolbars_spacing)}: $v px" },
                initialValue = toolbarsSpacing,
                valueRange = 0f..100f,
                steps = 99,
                onReset = { scope.launch { ToolbarsSettingsStore.setToolbarsSpacing(ctx, 8) } },
                onValueChangeFinished = { v ->
                    scope.launch { ToolbarsSettingsStore.setToolbarsSpacing(ctx, v) }
                }
            )
        }

        item {
            if (
                sourceList.any { it.toolbar == ToolBars.QUICK_ACTIONS && !it.enabled } ||
                quickActionsItems.any { it.action == GlobalNotesActions.SETTINGS && !it.enabled }
            ) {
                SwitchRow(
                    state = showBottomSettings,
                    text = stringResource(R.string.show_bottom_settings)
                ) {
                    if (it) scope.launch { UiSettingsStore.setShowBottomDeleteButton(ctx, true) }
                    else showDangerAboutUnabilityToAccessSettings = true
                }
            }
        }
    }

    if (showDangerAboutUnabilityToAccessSettings) {
        UserValidation(
            title = stringResource(R.string.are_you_really_sure),
            message = stringResource(R.string.you_wont_be_able_to_access_settings_anymore_unless),
            onCancel = {
                showDangerAboutUnabilityToAccessSettings = false
            }
        ) {
            scope.launch {
                UiSettingsStore.setShowBottomDeleteButton(ctx, false)
                showDangerAboutUnabilityToAccessSettings = false
            }
        }
    }
}

/** Creates a single composable item with all note action selectors. */
@Composable
private fun ActionsItems(
    ctx: Context,
    scope: CoroutineScope,
    settings: NoteActionSettings,
    actionsEntries: List<NotesActions>
) {
    val items = listOf(
        Triple(R.string.swipe_left_action, settings.leftAction, "left"),
        Triple(R.string.swipe_right_action, settings.rightAction, "right"),
        Triple(R.string.click_action, settings.clickAction, "click"),
        Triple(R.string.long_click_action, settings.longClickAction, "long"),
        Triple(R.string.left_button_action, settings.leftButtonAction, "leftBtn"),
        Triple(R.string.right_button_action, settings.rightButtonAction, "rightBtn")
    )

    items.forEach { (labelRes, selectedAction, key) ->
        ActionSelectorRow(
            label = stringResource(labelRes),
            options = actionsEntries,
            selected = selectedAction,
            optionLabel = { noteActionName(ctx, it) },
            onToggle = { enabled ->
                scope.launch {
                    setAction(ctx, key, if (enabled) defaultAction(key) else NotesActions.NONE)
                }
            },
            toggled = selectedAction != NotesActions.NONE
        ) { newAction ->
            scope.launch { setAction(ctx, key, newAction) }
        }
        Spacer(Modifier.height(12.dp))
    }
}

private suspend fun setAction(ctx: Context, key: String, action: NotesActions) {
    when (key) {
        "left" -> ActionSettingsStore.setSwipeLeftAction(ctx, action)
        "right" -> ActionSettingsStore.setSwipeRightAction(ctx, action)
        "click" -> ActionSettingsStore.setClickAction(ctx, action)
        "long" -> ActionSettingsStore.setLongClickAction(ctx, action)
        "leftBtn" -> ActionSettingsStore.setLeftButtonAction(ctx, action)
        "rightBtn" -> ActionSettingsStore.setRightButtonAction(ctx, action)
    }
}

private fun defaultAction(action: String): NotesActions {
    val settings = NoteActionSettings()
    return when (action) {
        "left" -> settings.leftAction
        "right" -> settings.rightAction
        "click" -> settings.clickAction
        "long" -> settings.longClickAction
        "leftBtn" -> settings.leftButtonAction
        "rightBtn" -> settings.rightButtonAction
        else -> settings.leftAction
    }
}