package org.elnix.notes.ui.security

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.notes.data.LockSettings
import org.elnix.notes.data.LockSettingsStore
import org.elnix.notes.security.BiometricManagerHelper
import java.time.Instant

@Composable
fun LockScreen(onUnlock: () -> Unit) {
    val ctx = LocalContext.current
    val activity = ctx as androidx.fragment.app.FragmentActivity
    val scope = rememberCoroutineScope()
    val settingsFlow = LockSettingsStore.getLockSettings(ctx)
    val settings by settingsFlow.collectAsState(initial = null)

    var isAuthenticating by remember { mutableStateOf(false) }
    var authFailed by remember { mutableStateOf(false) }

    // React to settings once they're loaded
    LaunchedEffect(settings) {
        if (settings == null) return@LaunchedEffect
        val s = settings!!

        val now = Instant.now().toEpochMilli()
        val diffMinutes =
            if (s.lastUnlockTimestamp == 0L) Long.MAX_VALUE
            else (now - s.lastUnlockTimestamp) / (1000 * 60)

        // only run authentication after settings are available
        if (diffMinutes >= s.lockTimeoutMinutes) {
            if (!isAuthenticating) {
                isAuthenticating = true
                authFailed = false
                startAuthentication(
                    activity = activity,
                    ctx = ctx,
                    settings = s,
                    scope = scope,
                    onSuccess = onUnlock,
                    onFailure = {
                        authFailed = true
                        isAuthenticating = false
                    }
                )
            }
        } else {
            onUnlock()
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
                        isAuthenticating = true
                        startAuthentication(
                            activity = activity,
                            ctx = ctx,
                            settings = settings ?: return@Button,
                            scope = scope,
                            onSuccess = onUnlock,
                            onFailure = {
                                authFailed = true
                                isAuthenticating = false
                            }
                        )
                    }) {
                        Text("Retry")
                    }
                }

                isAuthenticating -> {
                    Text(
                        text = "Authenticating...",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

private fun startAuthentication(
    activity: androidx.fragment.app.FragmentActivity,
    ctx: android.content.Context,
    settings: LockSettings,
    scope: kotlinx.coroutines.CoroutineScope,
    onSuccess: () -> Unit,
    onFailure: () -> Unit
) {
    if (!BiometricManagerHelper.canAuthenticate(
            activity,
            useBiometrics = settings.useBiometrics,
            useDeviceCredential = settings.useDeviceCredential
        )
    ) {
        onFailure()
        return
    }

    BiometricManagerHelper.authenticateUser(
        activity,
        useBiometrics = settings.useBiometrics,
        useDeviceCredential = settings.useDeviceCredential,
        title = "Unlock Notes",
        onSuccess = {
            scope.launch {
                LockSettingsStore.updateLockSettings(
                    ctx,
                    settings.copy(lastUnlockTimestamp = Instant.now().toEpochMilli())
                )
                // Wait for the write to finish before proceeding
            }.invokeOnCompletion { onSuccess() }
        },
        onFailure = { onFailure() }
    )
}
