package org.elnix.notes.ui.settings.appearance

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.elnix.notes.R
import org.elnix.notes.Routes
import org.elnix.notes.data.helpers.NoteViewType
import org.elnix.notes.data.settings.stores.UiSettingsStore
import org.elnix.notes.ui.helpers.ActionSelectorRow
import org.elnix.notes.ui.helpers.SwitchRow
import org.elnix.notes.ui.helpers.TextDivider
import org.elnix.notes.ui.helpers.settings.SettingsItem
import org.elnix.notes.ui.settings.SettingsLazyHeader

@Composable
fun AppearanceTab(
    ctx: Context,
    scope: CoroutineScope,
    navController: NavController,
    onBack: () -> Unit
) {

    val fullscreenApp by UiSettingsStore.getFullscreen(ctx).collectAsState(initial = false)
    val showNotesNumber by UiSettingsStore.getShowNotesNumber(ctx).collectAsState(initial = true)
    val notesViewType by UiSettingsStore.getNoteViewType(ctx).collectAsState(initial = NoteViewType.LIST)

    SettingsLazyHeader(
        title = stringResource(R.string.appearance),
        onBack = onBack,
        helpText = stringResource(R.string.appearance_tab_text),
        onReset = {
            scope.launch {
                UiSettingsStore.resetAll(ctx)
            }
        }
    ) {

        item {
            SettingsItem(
                title = stringResource(R.string.color_selector),
                icon = Icons.Default.ColorLens
            ) { navController.navigate(Routes.Settings.COLORS) }
        }

        item { TextDivider(stringResource(R.string.app_display)) }


        item {
            SwitchRow(
                fullscreenApp,
                stringResource(R.string.fullscreen_app),
            ) {
                scope.launch { UiSettingsStore.setFullscreen(ctx, it) }
            }
        }

        item {
            TextDivider(stringResource(R.string.notes_display))
        }

        item {
            SwitchRow(
                showNotesNumber,
                stringResource(R.string.show_notes_number),
            ) {
                scope.launch { UiSettingsStore.setShowNotesNumber(ctx, it) }
            }
        }

        item {
            ActionSelectorRow(
                label = stringResource(R.string.notes_view_type),
                options = NoteViewType.entries,
                selected = notesViewType,
                enabled = false,
                optionLabel = { it.name }
            ) {
                scope.launch { UiSettingsStore.setNoteViewType(ctx, it) }
            }
        }
    }
}
