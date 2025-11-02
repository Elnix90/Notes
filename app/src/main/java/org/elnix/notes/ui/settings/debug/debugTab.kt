package org.elnix.notes.ui.settings.debug

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.elnix.notes.R
import org.elnix.notes.Routes
import org.elnix.notes.SettingsItem
import org.elnix.notes.data.settings.stores.UiSettingsStore
import org.elnix.notes.ui.helpers.SettingsTitle
import org.elnix.notes.ui.helpers.SwitchRow
import org.elnix.notes.ui.helpers.TextDivider

@Composable
fun DebugTab(navController: NavController, onBack: (() -> Unit)) {
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current

    val isDebugModeEnabled by UiSettingsStore.getDebugMode(ctx).collectAsState(initial = false)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SettingsTitle(title = "Debug", onBack = onBack)

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

            SwitchRow(
                state = isDebugModeEnabled,
                text = "Activate Debug Mode",
                defaultValue = true
            ) {
                scope.launch{
                    UiSettingsStore.setDebugMode(ctx, false)
                }
                navController.popBackStack()
            }

            TextDivider(stringResource(R.string.debug_categories))


            SettingsItem(
                title = "Reminders",
                icon = Icons.Default.Alarm,
                onClick = { navController.navigate(Routes.Settings.DebugSub.REMINDERS) }
            )

            SettingsItem(
                title = "Notes",
                icon = Icons.AutoMirrored.Filled.FormatListBulleted,
                onClick = { navController.navigate(Routes.Settings.DebugSub.NOTES) }
            )

            SettingsItem(
                title = "Other",
                icon = Icons.Default.Build,
                onClick = { navController.navigate(Routes.Settings.DebugSub.OTHER) }
            )
        }
    }
}

