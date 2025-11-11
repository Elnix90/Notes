package org.elnix.notes.ui.settings.debug

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.elnix.notes.ui.settings.SettingsLazyHeader
import org.elnix.notes.ui.theme.AppObjectsColors

@Composable
fun OtherDebugTab(onBack: (() -> Unit)) {
    SettingsLazyHeader(
        title = "Debug -> Other",
        onBack = onBack,
        helpText = "Debug, too busy to make a translated explanation",
        onReset = null,
        resetText = null
    ) {
        item {
            OutlinedButton(
                onClick = { error("Crash Application") },
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
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

