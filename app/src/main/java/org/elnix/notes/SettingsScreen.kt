package org.elnix.notes

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DashboardCustomize
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.elnix.notes.data.settings.stores.UiSettingsStore
import org.elnix.notes.ui.NoteViewModel
import org.elnix.notes.ui.helpers.SettingsTitle
import org.elnix.notes.ui.helpers.TextDivider
import org.elnix.notes.ui.helpers.UserValidation
import org.elnix.notes.ui.settings.BackupTab
import org.elnix.notes.ui.settings.CustomisationTab
import org.elnix.notes.ui.settings.RemindersTab
import org.elnix.notes.ui.settings.appearance.AppearanceTab
import org.elnix.notes.ui.settings.appearance.ColorSelectorTab
import org.elnix.notes.ui.settings.debug.DebugTab
import org.elnix.notes.ui.settings.debug.NotesDebugTab
import org.elnix.notes.ui.settings.debug.OtherDebugTab
import org.elnix.notes.ui.settings.debug.RemindersDebugTab
import org.elnix.notes.ui.settings.language.LanguageTab
import org.elnix.notes.ui.settings.security.SecurityTab
import org.elnix.notes.ui.theme.adjustBrightness


@Composable
fun SettingsListScreen(navController: NavController) {

    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current

    val isDebugModeEnabled by UiSettingsStore.getDebugMode(ctx).collectAsState(initial = false)

    var timesClickedOnVersion by remember { mutableIntStateOf(0) }
    var showDebugModeUserValidation by remember { mutableStateOf(false) }

    var toast by remember { mutableStateOf<Toast?>(null) }

    val versionName = ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SettingsTitle(title = stringResource(R.string.settings)) {
            navController.popBackStack()
        }

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            SettingsItem(
                title = stringResource(R.string.appearance),
                icon = Icons.Default.DarkMode
            ) { navController.navigate(Routes.Settings.APPEARANCE) }

            SettingsItem(
                title = stringResource(R.string.customisation),
                icon = Icons.Default.DashboardCustomize
            ) { navController.navigate(Routes.Settings.CUSTOMISATION) }

            SettingsItem(
                title = stringResource(R.string.notification_reminders),
                icon = Icons.Default.Alarm
            ) { navController.navigate(Routes.Settings.REMINDER) }

            SettingsItem(
                title = stringResource(R.string.security_privacy),
                icon = Icons.Default.Shield
            ) { navController.navigate(Routes.Settings.SECURITY) }

            SettingsItem(
                title = stringResource(R.string.backup_restore),
                icon = Icons.Default.Backup,
                enabled = false,
                comingSoon = true
            ) { navController.navigate(Routes.Settings.BACKUP) }

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


            if (isDebugModeEnabled) {
                SettingsItem(
                    title = stringResource(R.string.debug),
                    icon = Icons.Default.BugReport
                ) { navController.navigate(Routes.Settings.DEBUG) }
            }


            TextDivider(stringResource(R.string.about))

            SettingsItem(
                title = stringResource(R.string.source_code),
                icon = Icons.Default.Code,
            ) {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = "https://github.com/Elnix90/Notes".toUri()
                }
                ctx.startActivity(intent)
            }

            SettingsItem(
                title = stringResource(R.string.check_for_update),
                icon = Icons.Default.Update
            ) {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = "https://github.com/Elnix90/Notes/releases/latest".toUri()
                }
                ctx.startActivity(intent)
            }

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


        Spacer(modifier = Modifier.weight(1f))


        Text(
            text = "${stringResource(R.string.version)} $versionName",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp, bottom = 16.dp)
                .clickable {
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

                        timesClickedOnVersion < 6 -> {
                            timesClickedOnVersion++
                            toast = Toast.makeText(
                                ctx,
                                "${7 - timesClickedOnVersion} more times to enable Debug Mode",
                                Toast.LENGTH_SHORT
                            )
                            toast?.show()
                        }

                        else -> {
                            showDebugModeUserValidation = true
                        }
                    }
                }
        )
    }

    if (showDebugModeUserValidation) {
        UserValidation(
            title = stringResource(R.string.are_you_sure),
            message = stringResource(R.string.debug_mode_will_be_enabled),
            onCancel = { showDebugModeUserValidation = false}
        ) {
            scope.launch{
                UiSettingsStore.setDebugMode(ctx, true)
                showDebugModeUserValidation = false
            }
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    enabled: Boolean = true,
    comingSoon: Boolean = false,
    icon: ImageVector? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled) { onClick() }
            .background(
                color = MaterialTheme.colorScheme.surface.adjustBrightness(if (enabled) 1f else 0.5f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(15.dp)) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.adjustBrightness(if (enabled) 1f else 0.5f)
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.adjustBrightness(if (enabled) 1f else 0.5f)
            )
            if (comingSoon) {
                Spacer(Modifier.weight(1f))
                Text(
                    text = stringResource(R.string.coming_soon),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.adjustBrightness(0.5f)
                )
            }
        }
    }
}


@Composable
fun AppearanceSettingsScreen(navController: NavController) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    AppearanceTab(ctx, scope, navController) {
        navController.popBackStack()
    }
}

@Composable
fun ColorSelectorSettingsScreen(navController: NavController) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    ColorSelectorTab(ctx, scope) {
        navController.popBackStack()
    }
}
@Composable
fun RemindersSettingsScreen(navController: NavController) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    RemindersTab(ctx, scope) {
        navController.popBackStack()
    }
}

@Composable
fun SecuritySettingsScreen(navController: NavController) {
    SecurityTab {
        navController.popBackStack()
    }
}

@Composable
fun BackupSettingsScreen(navController: NavController) {
    val ctx = LocalContext.current
    BackupTab(ctx) {
        navController.popBackStack()
    }
}

@Composable
fun LanguageSettingsScreen(navController: NavController) {
    LanguageTab {
        navController.popBackStack()
    }
}

@Composable
fun CustomisationSettingsScreen(navController: NavController) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    CustomisationTab(ctx, scope) {
        navController.popBackStack()
    }
}

@Composable
fun DebugSettingsScreen(navController: NavController) {
    DebugTab(navController) {
        navController.popBackStack()
    }
}

@Composable
fun DebugReminderSettingsScreen(navController: NavController, vm : NoteViewModel) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    RemindersDebugTab(ctx, scope, vm) {
        navController.popBackStack()
    }
}

@Composable
fun DebugNotesSettingsScreen(navController: NavController, vm : NoteViewModel) {
    NotesDebugTab(vm) {
        navController.popBackStack()
    }
}

@Composable
fun OtherSettingsScreen(navController: NavController) {
    OtherDebugTab {
        navController.popBackStack()
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private fun openSystemLanguageSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APP_LOCALE_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}