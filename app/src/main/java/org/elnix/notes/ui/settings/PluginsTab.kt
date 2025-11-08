package org.elnix.notes.ui.settings

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.elnix.notes.R
import org.elnix.notes.data.settings.stores.PluginsSettingsStore
import org.elnix.notes.security.BiometricManagerHelper
import org.elnix.notes.ui.helpers.SettingsTitle
import org.elnix.notes.ui.helpers.SwitchRow
import org.elnix.notes.ui.helpers.TextDivider

@Composable
fun PluginsTab(ctx: Context, scope: CoroutineScope, onBack: (() -> Unit)) {
    val allowAlphaLMAccess by PluginsSettingsStore.getAllowAlphaLMAccess(ctx).collectAsState(initial = false)

    val activity = ctx as androidx.fragment.app.FragmentActivity

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(
                WindowInsets.systemBars
                    .asPaddingValues()
            )
            .padding(horizontal = 16.dp, vertical = 5.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SettingsTitle(title = stringResource(R.string.plugins), onBack = onBack)

        TextDivider(stringResource(R.string.connected_apps))

        // AlphaLM App Access Toggle
        SwitchRow(
            state = allowAlphaLMAccess,
            text = stringResource(R.string.alphallm_app),
            onCheck = { newState ->
                if (newState) {
                    scope.launch {
                        BiometricManagerHelper.authenticateUser(
                            activity = activity,
                            useBiometrics = true,
                            useDeviceCredential = true,
                            title = "Verification",
                            onSuccess = {
                                scope.launch {
                                    PluginsSettingsStore.setAllowAlphaLMAccess(ctx, true)
                                }
                            },
                            onFailure = {}
                        )
                    }
                } else scope.launch {
                    PluginsSettingsStore.setAllowAlphaLMAccess(ctx, false)
                }
            }
        )
    }
}
