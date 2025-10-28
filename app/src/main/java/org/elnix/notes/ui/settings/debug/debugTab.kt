package org.elnix.notes.ui.settings.debug

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import org.elnix.notes.ui.helpers.SettingsTitle
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.elnix.notes.Routes
import org.elnix.notes.SettingsItem
import org.elnix.notes.ui.NoteViewModel

@Composable
fun DebugTab(vm: NoteViewModel, navController: NavController, onBack: (() -> Unit)) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SettingsTitle("Debug", onBack)

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

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

