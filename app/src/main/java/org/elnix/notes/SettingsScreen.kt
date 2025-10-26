package org.elnix.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DashboardCustomize
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.elnix.notes.ui.NoteViewModel
import org.elnix.notes.ui.helpers.SettingsTitle
import org.elnix.notes.ui.settings.appearance.AppearanceTab
import org.elnix.notes.ui.settings.BackupTab
import org.elnix.notes.ui.settings.CustomisationTab
import org.elnix.notes.ui.settings.DebugTab
import org.elnix.notes.ui.settings.RemindersTab
import org.elnix.notes.ui.settings.appearance.ColorSelectorTab
import org.elnix.notes.ui.settings.security.SecurityTab


@Composable
fun SettingsListScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SettingsTitle("Settings") {
            navController.popBackStack()
        }

        SettingsItem(
            title = "Appearance",
            icon = Icons.Default.DarkMode,
            onClick = { navController.navigate(Screen.Settings.appearanceTab()) }
        )
        SettingsItem(
            title = "Customisation",
            icon = Icons.Default.DashboardCustomize,
            onClick = { navController.navigate(Screen.Settings.customisationTab()) }
        )
        SettingsItem(
            title = "Reminders",
            icon = Icons.Default.Alarm,
            onClick = { navController.navigate(Screen.Settings.reminderTab()) }
        )
        SettingsItem(
            title = "Security",
            icon = Icons.Default.Shield,
            onClick = { navController.navigate(Screen.Settings.securityTab()) }
        )
        SettingsItem(
            title = "Backup",
            icon = Icons.Default.Backup,
            onClick = { navController.navigate(Screen.Settings.backupTab()) }
        )
        SettingsItem(
            title = "Debug",
            icon = Icons.Default.BugReport,
            onClick = { navController.navigate(Screen.Settings.debugTab()) }
        )
    }
}

@Composable
fun SettingsItem(
    title: String,
    onClick: () -> Unit,
    icon: ImageVector? = null // optional leading icon
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(
                color = MaterialTheme.colorScheme.surface,
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
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
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
fun BackupSettingsScreen(navController: NavController) {
    val ctx = LocalContext.current
    BackupTab(ctx) {
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
fun CustomisationSettingsScreen(navController: NavController) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    CustomisationTab(ctx, scope) {
        navController.popBackStack()
    }
}

@Composable
fun DebugSettingsScreen(navController: NavController, vm : NoteViewModel) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    DebugTab(ctx, scope, vm) {
        navController.popBackStack()
    }
}