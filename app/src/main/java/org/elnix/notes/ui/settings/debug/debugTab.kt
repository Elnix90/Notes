package org.elnix.notes.ui.settings.debug

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.elnix.notes.R
import org.elnix.notes.Routes
import org.elnix.notes.data.settings.stores.DebugSettingsStore
import org.elnix.notes.ui.helpers.SwitchRow
import org.elnix.notes.ui.helpers.TextDivider
import org.elnix.notes.ui.helpers.settings.SettingsItem
import org.elnix.notes.ui.helpers.settings.SettingsLazyHeader

@Composable
fun DebugTab(navController: NavController, onBack: (() -> Unit)) {
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current

    val isDebugModeEnabled by DebugSettingsStore.getDebugMode(ctx).collectAsState(initial = false)

    SettingsLazyHeader(
        title = stringResource(R.string.debug),
        onBack = onBack,
        helpText = "Debug, too busy to make a translated explanation",
        onReset = null,
        resetText = null
    ) {

        item{
            SwitchRow(
                state = isDebugModeEnabled,
                text = "Activate Debug Mode",
                defaultValue = true
            ) {
                scope.launch {
                    DebugSettingsStore.setDebugMode(ctx, false)
                }
                navController.popBackStack()
            }
        }

        item {
            TextDivider(stringResource(R.string.debug_categories))
        }

        item {
            SettingsItem(
                title = "Reminders",
                icon = Icons.Default.Alarm,
                onClick = { navController.navigate(Routes.Settings.DebugSub.REMINDERS) }
            )
        }

        item {
            SettingsItem(
                title = "Notes",
                icon = Icons.AutoMirrored.Filled.FormatListBulleted,
                onClick = { navController.navigate(Routes.Settings.DebugSub.NOTES) }
            )
        }

        item {
            SettingsItem(
                title = "User Confirm",
                icon = Icons.Default.VerifiedUser,
                onClick = { navController.navigate(Routes.Settings.DebugSub.USER_CONFIRM) }
            )
        }

        item {
            SettingsItem(
                title = "Other",
                icon = Icons.Default.Build,
                onClick = { navController.navigate(Routes.Settings.DebugSub.OTHER) }
            )
        }
    }
}

