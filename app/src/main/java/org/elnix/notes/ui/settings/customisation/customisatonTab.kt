package org.elnix.notes.ui.settings.customisation

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.elnix.notes.R
import org.elnix.notes.Routes
import org.elnix.notes.data.helpers.NoteActionSettings
import org.elnix.notes.data.helpers.NotesActions
import org.elnix.notes.data.settings.stores.ActionSettingsStore
import org.elnix.notes.data.settings.stores.UiSettingsStore
import org.elnix.notes.ui.helpers.ActionSelectorRow
import org.elnix.notes.ui.helpers.SwitchRow
import org.elnix.notes.ui.helpers.TextDivider
import org.elnix.notes.ui.helpers.settings.SettingsItem
import org.elnix.notes.ui.helpers.settings.SettingsTitle

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

    val showTagSelector by UiSettingsStore.getShowTagSelector(ctx).collectAsState(initial = true)
    val showTagsInNotes by UiSettingsStore.getShowTagsInNotes(ctx).collectAsState(initial = true)


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(WindowInsets.systemBars.asPaddingValues())
            .padding(horizontal = 16.dp)
            .imePadding()
    ) {

        Surface(color = MaterialTheme.colorScheme.background, tonalElevation = 3.dp) {
            SettingsTitle(title = stringResource(R.string.customisation)) { onBack() }
            Spacer(Modifier.height(20.dp))
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(bottom = 400.dp)
        ) {

            item { TextDivider(stringResource(R.string.note_actions)) }

            // --- Swipe Left ---
            item {
                ActionSelectorRow(
                    label = stringResource(R.string.swipe_left_action),
                    options = NotesActions.entries,
                    selected = settings.leftAction,
                    optionLabel = { it.name }
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
                    optionLabel = { it.name }
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
                    optionLabel = { it.name }
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
                    optionLabel = { it.name }
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
                    optionLabel = { it.name }
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

            // New Tags Category
            item { TextDivider(stringResource(R.string.tags)) }

            item {
                SwitchRow(
                    showTagSelector,
                    stringResource(R.string.show_tag_selector),
                ) {
                    scope.launch { UiSettingsStore.setShowTagSelector(ctx, it) }
                }
            }

            item {
                SwitchRow(
                    showTagsInNotes,
                    stringResource(R.string.show_tags_in_notes),
                ) {
                    scope.launch { UiSettingsStore.setShowTagsInNotes(ctx, it) }
                }
            }

            item {
                SettingsItem(
                    title = stringResource(R.string.toolbars),
                    icon = Icons.Default.Route
                ) {
                    navController.navigate(Routes.Settings.CustomisationSub.TOOLBARS)
                }
            }
        }
    }
}
