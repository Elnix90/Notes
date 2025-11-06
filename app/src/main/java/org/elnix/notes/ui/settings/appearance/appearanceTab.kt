package org.elnix.notes.ui.settings.appearance

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.elnix.notes.R
import org.elnix.notes.Routes
import org.elnix.notes.SettingsItem
import org.elnix.notes.data.helpers.NoteViewType
import org.elnix.notes.data.settings.ShowNavBarActions
import org.elnix.notes.data.settings.stores.UiSettingsStore
import org.elnix.notes.ui.helpers.ActionSelectorRow
import org.elnix.notes.ui.helpers.SettingsTitle
import org.elnix.notes.ui.helpers.SwitchRow
import org.elnix.notes.ui.helpers.TextDivider

@Composable
fun AppearanceTab(
    ctx: Context,
    scope: CoroutineScope,
    navController: NavController,
    onBack: () -> Unit
) {
    val showNavbarLabel by UiSettingsStore.getShowBottomNavLabelsFlow(ctx)
        .collectAsState(initial = ShowNavBarActions.ALWAYS)

    val fullscreenApp by UiSettingsStore.getFullscreen(ctx).collectAsState(initial = false)
    val showNotesNumber by UiSettingsStore.getShowNotesNumber(ctx).collectAsState(initial = true)
    val notesViewType by UiSettingsStore.getNoteViewType(ctx).collectAsState(initial = NoteViewType.LIST)
    val floatingToolbars by UiSettingsStore.getFloatingToolbars(ctx).collectAsState(initial = true)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                WindowInsets.systemBars
                    .asPaddingValues()
            )
            .padding(horizontal = 16.dp, vertical = 5.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SettingsTitle(title = stringResource(R.string.appearance), onBack = onBack)

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            SettingsItem(
                title = stringResource(R.string.color_selector),
                icon = Icons.Default.ColorLens
            ) { navController.navigate(Routes.Settings.COLORS) }

            TextDivider(stringResource(R.string.app_display))

            ActionSelectorRow(
                label = stringResource(R.string.show_navigation_bar_labels),
                options = ShowNavBarActions.entries,
                selected = showNavbarLabel,
                optionLabel = { it.name}
            ) {
                scope.launch { UiSettingsStore.setShowBottomNavLabelsFlow(ctx, it) }
            }

            SwitchRow(
                fullscreenApp,
                stringResource(R.string.fullscreen_app),
            ) {
                scope.launch { UiSettingsStore.setFullscreen(ctx, it) }
            }


            SwitchRow(
                showNotesNumber,
                stringResource(R.string.show_notes_number),
            ) {
                scope.launch { UiSettingsStore.setShowNotesNumber(ctx, it) }
            }

            TextDivider(stringResource(R.string.notes_display))

            ActionSelectorRow(
                label = stringResource(R.string.notes_view_type),
                options = NoteViewType.entries,
                selected = notesViewType,
                enabled = false,
                optionLabel = { it.name}
            ) {
                scope.launch { UiSettingsStore.setNoteViewType(ctx, it) }
            }

            SwitchRow(
                floatingToolbars,
                stringResource(R.string.floating_toolbars),
            ) {
                scope.launch { UiSettingsStore.setFloatingToolbars(ctx, it) }
            }
        }
    }
}
