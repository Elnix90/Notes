package org.elnix.notes.ui.security

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.launch
import org.elnix.notes.data.LockSettings
import org.elnix.notes.data.settings.LockSettingsStore
import org.elnix.notes.security.BiometricManagerHelper
import java.time.Instant

@Composable
fun LockScreen(activity: FragmentActivity, onUnlock: () -> Unit) {
    val ctx = LocalContext.current
//    val activity = ctx.findFragmentActivity() ?: return

    val scope = rememberCoroutineScope()
    val settingsFlow = LockSettingsStore.getLockSettings(ctx)
    val settings by settingsFlow.collectAsState(initial = null)

    var isAuthenticating by remember { mutableStateOf(false) }
    var authFailed by remember { mutableStateOf(false) }
    var authenticationStarted by remember { mutableStateOf(false) }


    LaunchedEffect(settings) {
        if (settings != null && !authenticationStarted) {
            authenticationStarted = true
            startAuthentication(
                activity,
                ctx,
                settings!!,
                scope,
                onSuccess = onUnlock,
                onFailure = {
                    authFailed = true
                    isAuthenticating = false
                }
            )
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when {
                authFailed -> {
                    Text(
                        "Authentication failed or cancelled",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = {
                        authFailed = false
                        settings?.let {
                            startAuthentication(
                                activity,
                                ctx,
                                it,
                                scope,
                                onSuccess = onUnlock,
                                onFailure = {
                                    authFailed = true
                                    isAuthenticating = false
                                }
                            )
                        }
                    }) {
                        Text("Retry")
                    }
                }
                isAuthenticating -> {
                    Text(
                        "Authenticating...",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

private fun startAuthentication(
    activity: FragmentActivity,
    ctx: android.content.Context,
    settings: LockSettings,
    scope: kotlinx.coroutines.CoroutineScope,
    onSuccess: () -> Unit,
    onFailure: () -> Unit
) {

    val now = Instant.now().toEpochMilli()
    val diffSeconds =
        if (settings.lastUnlockTimestamp == 0L) Long.MAX_VALUE
        else (now - settings.lastUnlockTimestamp) / (1000 * 60)

    val canAuth = BiometricManagerHelper.canAuthenticate(
        activity,
        useBiometrics = settings.useBiometrics,
        useDeviceCredential = settings.useDeviceCredential
    )

    // If no valid authentication method or if user unlocked recently, skip authentication
    if (!canAuth || diffSeconds < settings.lockTimeoutSeconds) {
        onSuccess()
        return
    }

    scope.launch {
        BiometricManagerHelper.authenticateUser(
            activity,
            settings.useBiometrics,
            settings.useDeviceCredential,
            title = "Unlock Notes",
            onSuccess = {
                scope.launch {
                    LockSettingsStore.updateLockSettings(
                        ctx,
                        settings.copy(lastUnlockTimestamp = Instant.now().toEpochMilli())
                    )
                }.invokeOnCompletion { onSuccess() }
            },
            onFailure = { onFailure() }
        )
    }
}
