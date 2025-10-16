// file: org/elnix/notes/SettingsScreen.kt
package org.elnix.notes

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.notes.data.SettingsStore

@Composable
fun SettingsScreen() {
    val ctx = LocalContext.current
    val isDarkFlow = remember { SettingsStore.isDarkFlow(ctx) }
    val isDark by isDarkFlow.collectAsState(initial = false)
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Dark theme")
            Switch(checked = isDark, onCheckedChange = {
                scope.launch { SettingsStore.setDark(ctx, it) }
            })
        }
    }
}
