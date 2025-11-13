package org.elnix.notes.ui.settings.debug

import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.notes.data.settings.stores.DebugSettingsStore
import org.elnix.notes.ui.helpers.SwitchRow
import org.elnix.notes.ui.settings.SettingsLazyHeader
import org.elnix.notes.ui.theme.AppObjectsColors

@Composable
fun OtherDebugTab(onBack: (() -> Unit)) {

    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val forceAppLanguageSelector by DebugSettingsStore.getForceAppLanguageSelector(ctx).collectAsState(initial = false)
    val isForceSwitchToggled = forceAppLanguageSelector || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU

    SettingsLazyHeader(
        title = "Debug -> Other",
        onBack = onBack,
        helpText = "Debug, too busy to make a translated explanation",
        onReset = null,
        resetText = null
    ) {
        item {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Text(
                    text = "Check this to force the app's language selector instead of the android's one",
                    color = MaterialTheme.colorScheme.onBackground
                )
            } else {
                Text(
                    text = "Since you're under android 13, or code name TIRAMISU you can't use the android language selector and you're blocked with the app custom one.",
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        item {
            SwitchRow(
                state = isForceSwitchToggled ,
                text = "Force app language selector",
                enabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            ) { scope.launch { DebugSettingsStore.setForceAppLanguageSelector(ctx, it) } }
        }

        item {
            OutlinedButton(
                onClick = { error("Crash Application") },
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                colors = AppObjectsColors.cancelButtonColors()
            ) {
                Text("Crash Application")
            }
        }
    }
}

