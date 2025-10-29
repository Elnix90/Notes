package org.elnix.notes.ui.settings.appearance

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.elnix.notes.SettingsItem
import org.elnix.notes.data.settings.ShowNavBarActions
import org.elnix.notes.data.settings.UiSettingsStore
import org.elnix.notes.ui.helpers.ActionSelectorRow
import org.elnix.notes.ui.helpers.SettingsTitle

@Composable
fun AppearanceTab(
    ctx: Context,
    scope: CoroutineScope,
    navController: NavController,
    onBack: () -> Unit
) {
    val showNavbarLabel by UiSettingsStore.getShowBottomNavLabelsFlow(ctx)
        .collectAsState(initial = ShowNavBarActions.ALWAYS)


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SettingsTitle("Appearance", onBack)

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            SettingsItem(
                title = "Color Selector",
                icon = Icons.Default.ColorLens,
                onClick = { navController.navigate("settings/appearance/colors") }
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outline)

            ActionSelectorRow(
                label = "Show Navigation Bar Labels",
                options = ShowNavBarActions.entries,
                selected = showNavbarLabel,
                optionLabel = { it.name}
            ) {
                scope.launch { UiSettingsStore.setShowBottomNavLabelsFlow(ctx, it) }
            }
        }
    }
}
