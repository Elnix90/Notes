package org.elnix.notes.ui.settings.plugins

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.elnix.notes.R
import org.elnix.notes.data.settings.stores.PluginsSettingsStore
import org.elnix.notes.security.BiometricManagerHelper
import org.elnix.notes.ui.helpers.SwitchRow
import org.elnix.notes.ui.settings.SettingsLazyHeader

@Composable
fun PluginsTab(ctx: Context, scope: CoroutineScope, onBack: (() -> Unit)) {
    val allowAlphaLMAccess by PluginsSettingsStore.getAllowAlphaLMAccess(ctx).collectAsState(initial = false)

    val activity = ctx as FragmentActivity

    SettingsLazyHeader(
        title = stringResource(R.string.plugins),
        onBack = onBack
    ) {

        // AlphaLM App Access Toggle
        item {
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
}
