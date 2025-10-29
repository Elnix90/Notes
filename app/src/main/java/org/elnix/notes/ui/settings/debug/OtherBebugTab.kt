package org.elnix.notes.ui.settings.debug

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.elnix.notes.ui.helpers.SettingsTitle
import org.elnix.notes.ui.theme.AppObjectsColors

@Composable
fun OtherDebugTab(onBack: (() -> Unit)) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SettingsTitle(title = "Debug -> Other", onBack = onBack)

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(
                onClick = { error("Crash Application") },
                modifier = Modifier.fillMaxWidth(),
                colors = AppObjectsColors.cancelButtonColors()
            ) {
                Text(
                    text = "Crash Application",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

