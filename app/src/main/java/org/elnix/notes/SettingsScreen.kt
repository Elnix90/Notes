package org.elnix.notes

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DashboardCustomize
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.elnix.notes.data.helpers.ToolBars
import org.elnix.notes.data.settings.dataStore
import org.elnix.notes.data.settings.stores.DebugSettingsStore
import org.elnix.notes.data.settings.stores.UserConfirmSettingsStore
import org.elnix.notes.ui.NoteViewModel
import org.elnix.notes.ui.helpers.TextDivider
import org.elnix.notes.ui.helpers.UserValidation
import org.elnix.notes.ui.helpers.settings.ContributorItem
import org.elnix.notes.ui.helpers.settings.SettingsItem
import org.elnix.notes.ui.settings.SettingsLazyHeader
import org.elnix.notes.ui.settings.appearance.AppearanceTab
import org.elnix.notes.ui.settings.appearance.ColorSelectorTab
import org.elnix.notes.ui.settings.backup.BackupTab
import org.elnix.notes.ui.settings.customisation.CustomisationTab
import org.elnix.notes.ui.settings.customisation.ToolbarCustomisationTab
import org.elnix.notes.ui.settings.customisation.ToolbarsOrderTab
import org.elnix.notes.ui.settings.debug.DebugTab
import org.elnix.notes.ui.settings.debug.NotesDebugTab
import org.elnix.notes.ui.settings.debug.OtherDebugTab
import org.elnix.notes.ui.settings.debug.RemindersDebugTab
import org.elnix.notes.ui.settings.debug.UserConfirmDebugTab
import org.elnix.notes.ui.settings.language.LanguageTab
import org.elnix.notes.ui.settings.plugins.PluginsTab
import org.elnix.notes.ui.settings.reminders.NotificationsCustomisationTab
import org.elnix.notes.ui.settings.reminders.RemindersTab
import org.elnix.notes.ui.settings.security.SecurityTab


@Composable
fun SettingsListScreen(
    navController: NavController,
    onBack: () -> Unit
) {

    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current

    val isDebugModeEnabled by DebugSettingsStore.getDebugMode(ctx).collectAsState(initial = false)

    var timesClickedOnVersion by remember { mutableIntStateOf(0) }
    val showUserConfirmEnableDebug by UserConfirmSettingsStore.getShowEnableDebug(ctx)
        .collectAsState(initial = true)
    var showDebugModeUserValidation by remember { mutableStateOf(false) }

    var toast by remember { mutableStateOf<Toast?>(null) }

    val versionName = ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName

    BackHandler { onBack() }

    SettingsLazyHeader(
        title = stringResource(R.string.settings),
        onBack = { onBack() },
        helpText = stringResource(R.string.main_settings_text),
        resetText = stringResource(R.string.reset_all_default_settings_text),
        onReset = {
            scope.launch {
                ctx.dataStore.edit { prefs ->
                    prefs.clear()
                }
            }
        }
    ) {

        item { TextDivider(stringResource(R.string.main_settings)) }

        item {
            SettingsItem(
                title = stringResource(R.string.appearance),
                icon = Icons.Default.DarkMode
            ) { navController.navigate(Routes.Settings.APPEARANCE) }
        }

        item {
            SettingsItem(
                title = stringResource(R.string.customisation),
                icon = Icons.Default.DashboardCustomize
            ) { navController.navigate(Routes.Settings.CUSTOMISATION) }
        }
        item {
            SettingsItem(
                title = stringResource(R.string.notification_reminders),
                icon = Icons.Default.Alarm
            ) { navController.navigate(Routes.Settings.REMINDER) }
        }

        item {
            SettingsItem(
                title = stringResource(R.string.security_privacy),
                icon = Icons.Default.Shield
            ) { navController.navigate(Routes.Settings.SECURITY) }
        }

        item {
            SettingsItem(
                title = stringResource(R.string.backup_restore),
                icon = Icons.Default.Backup,
            ) { navController.navigate(Routes.Settings.BACKUP) }
        }

        item {
            SettingsItem(
                title = stringResource(R.string.settings_language_title),
                icon = Icons.Default.Language,
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        openSystemLanguageSettings(ctx)
                    } else {
                        navController.navigate(Routes.Settings.LANGUAGE)
                    }
                }
            )
        }

        item{
            SettingsItem(
                title = stringResource(R.string.plugins),
                icon = Icons.Default.Extension
            ) { navController.navigate(Routes.Settings.PLUGINS) }
        }

        if (isDebugModeEnabled) {
            item {
                SettingsItem(
                    title = stringResource(R.string.debug),
                    icon = Icons.Default.BugReport
                ) { navController.navigate(Routes.Settings.DEBUG) }
            }
        }


        item { TextDivider(stringResource(R.string.about)) }

        item {
            SettingsItem(
                title = stringResource(R.string.source_code),
                icon = Icons.Default.Code,
            ) {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = "https://github.com/Elnix90/Notes".toUri()
                }
                ctx.startActivity(intent)
            }
        }

        item {
            SettingsItem(
                title = stringResource(R.string.check_for_update),
                icon = Icons.Default.Update
            ) {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = "https://github.com/Elnix90/Notes/releases/latest".toUri()
                }
                ctx.startActivity(intent)
            }
        }

        item {
            SettingsItem(
                title = stringResource(R.string.report_a_bug),
                icon = Icons.Default.ReportProblem
            ) {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = "https://github.com/Elnix90/Notes/issues/new".toUri()
                }
                ctx.startActivity(intent)
            }
        }


        item {
            TextDivider(
                stringResource(R.string.contributors),
                Modifier.padding(horizontal = 60.dp)
            )
        }

        item {
            ContributorItem(
                name = "Elnix90",
                imageRes = R.drawable.elnix90,
                description = stringResource(R.string.app_developer),
                githubUrl = "https://github.com/Elnix90"
            )
        }

        item {
            ContributorItem(
                name = "LuckyTheCookie",
                imageRes = R.drawable.lucky_the_cookie,
                description = stringResource(R.string.thanks_for_alphallm),
                githubUrl = "https://github.com/LuckyTheCookie"
            )
        }


        item {
            Text(
                text = "${stringResource(R.string.version)} $versionName",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 16.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        toast?.cancel()

                        when {
                            isDebugModeEnabled -> {
                                toast = Toast.makeText(
                                    ctx,
                                    "Debug Mode is already enabled",
                                    Toast.LENGTH_SHORT
                                )
                                toast?.show()
                            }

                            !showUserConfirmEnableDebug -> scope.launch {
                                DebugSettingsStore.setDebugMode(
                                    ctx,
                                    true
                                )
                            }

                            timesClickedOnVersion < 6 -> {
                                timesClickedOnVersion++
                                if (timesClickedOnVersion > 2) {
                                    toast = Toast.makeText(
                                        ctx,
                                        "${7 - timesClickedOnVersion} more times to enable Debug Mode",
                                        Toast.LENGTH_SHORT
                                    )
                                }
                                toast?.show()
                            }

                            else -> {
                                showDebugModeUserValidation = true
                            }
                        }
                    }
            )
        }
    }

    if (showDebugModeUserValidation) {
        UserValidation(
            title = stringResource(R.string.are_you_sure),
            message = stringResource(R.string.debug_mode_will_be_enabled),
            onCancel = { showDebugModeUserValidation = false},
            doNotRemindMeAgain = {
                scope.launch { UserConfirmSettingsStore.setShowEnableDebug(ctx,false) }
            }
        ) {
            scope.launch{
                DebugSettingsStore.setDebugMode(ctx, true)
                showDebugModeUserValidation = false
            }
        }
    }
}




@Composable
fun AppearanceSettingsScreen(navController: NavController) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    AppearanceTab(ctx, scope, navController) {
        navController.navigate(Routes.Settings.ROOT)
    }
}

@Composable
fun ColorSelectorSettingsScreen(navController: NavController) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    ColorSelectorTab(ctx, scope) {
        navController.navigate(Routes.Settings.APPEARANCE)
    }
}
@Composable
fun RemindersSettingsScreen(navController: NavController, activity: FragmentActivity) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    RemindersTab(ctx, activity, scope, navController) {
        navController.navigate(Routes.Settings.ROOT)
    }
}

@Composable
fun NotificationsSettingsScreen(navController: NavController) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    NotificationsCustomisationTab(ctx, scope) {
        navController.navigate(Routes.Settings.REMINDER)
    }
}

@Composable
fun SecuritySettingsScreen(navController: NavController) {
    SecurityTab {
        navController.navigate(Routes.Settings.ROOT)
    }
}

@Composable
fun BackupSettingsScreen(navController: NavController) {
    val ctx = LocalContext.current
    BackupTab(ctx) {
        navController.navigate(Routes.Settings.ROOT)
    }
}

@Composable
fun LanguageSettingsScreen(navController: NavController) {
    LanguageTab {
        navController.navigate(Routes.Settings.ROOT)
    }
}

@Composable
fun PluginsSettingsScreen(navController: NavController) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    PluginsTab(ctx, scope) {
        navController.navigate(Routes.Settings.ROOT)
    }
}

@Composable
fun CustomisationSettingsScreen(navController: NavController) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    CustomisationTab(ctx, scope, navController) {
        navController.navigate(Routes.Settings.ROOT)
    }
}

@Composable
fun ToolbarsOrderSettingsScreen(navController: NavController) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    ToolbarsOrderTab(ctx, scope, navController) {
        navController.navigate(Routes.Settings.CUSTOMISATION)
    }
}

@Composable
fun ToolbarsCustomisationSettingsScreen(navController: NavController, toolbar: ToolBars) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    ToolbarCustomisationTab(ctx, scope, toolbar) {
        navController.navigate(Routes.Settings.CustomisationSub.TOOLBARS)
    }
}

@Composable
fun DebugSettingsScreen(navController: NavController) {
    DebugTab(navController) {
        navController.navigate(Routes.Settings.ROOT)
    }
}

@Composable
fun DebugReminderSettingsScreen(navController: NavController, vm : NoteViewModel) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    RemindersDebugTab(ctx, scope, vm) {
        navController.navigate(Routes.Settings.DEBUG)
    }
}

@Composable
fun DebugNotesSettingsScreen(navController: NavController, vm : NoteViewModel) {
    val scope = rememberCoroutineScope()
    NotesDebugTab(vm, scope) {
        navController.navigate(Routes.Settings.DEBUG)
    }
}

@Composable
fun OtherSettingsScreen(navController: NavController) {
    OtherDebugTab {
        navController.navigate(Routes.Settings.DEBUG)
    }
}

@Composable
fun UserConfirmSettingsScreen(navController: NavController) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    UserConfirmDebugTab(ctx, scope) {
        navController.navigate(Routes.Settings.DEBUG)
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private fun openSystemLanguageSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APP_LOCALE_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}