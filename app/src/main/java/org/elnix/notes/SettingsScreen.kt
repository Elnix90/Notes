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
import androidx.compose.material.icons.filled.DarkMode
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
import org.elnix.notes.settings.AppearanceTab
import org.elnix.notes.settings.BackupTab
import org.elnix.notes.settings.RemindersTab


@Composable
fun SettingsListScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        SettingsItem(
            title = "Appearance",
            icon = Icons.Default.DarkMode,
            onClick = { navController.navigate("settings/appearance") }
        )
        SettingsItem(
            title = "Reminders",
            icon = Icons.Default.Alarm,
            onClick = { navController.navigate("settings/reminders") }
        )
        SettingsItem(
            title = "Backup",
            icon = Icons.Default.Backup,
            onClick = { navController.navigate("settings/backup") }
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
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
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
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}


@Composable
fun AppearanceSettingsScreen(navController: NavController) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    AppearanceTab(ctx, scope) {
        navController.popBackStack()
    }
}

@Composable
fun RemindersSettingsScreen() {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    RemindersTab(ctx, scope)
}

@Composable
fun BackupSettingsScreen() {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    BackupTab(ctx, scope)
}


