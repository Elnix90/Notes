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
    val settings by LockSettingsStore.getLockSettings(ctx)
        .collectAsState(initial = LockSettings())

    var isAuthenticating by remember { mutableStateOf(false) }
    var showRetry by remember { mutableStateOf(true) }


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
            if (showRetry) {
                Text(
                    "Authentication failed or cancelled",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(16.dp))
                Button(onClick = {
                    showRetry = false
                    isAuthenticating = true
                    startAuthentication(
                        activity = activity,
                        ctx = ctx,
                        settings = settings,
                        scope = scope,
                        onSuccess = onUnlock,
                        onError = {
                            isAuthenticating = false
                            showRetry = true
                        }
                    )
                }) {
                    Text("Retry")
                }
            } else {
                Text(
                    text = if (isAuthenticating) "Authenticating..." else "Lock Screen",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }

//    LaunchedEffect(settings) {
//        if (!isAuthenticating) {
//            isAuthenticating = true
//            val now = Instant.now().toEpochMilli()
//            val diff = (now - settings.lastUnlockTimestamp) / (1000 * 60)
//            if (diff < settings.lockTimeoutMinutes) {
//                onUnlock()
//            } else {
//                startAuthentication(
//                    activity = activity,
//                    ctx = ctx,
//                    settings = settings,
//                    scope = scope,
//                    onSuccess = onUnlock,
//                    onError = {
//                        isAuthenticating = false
//                        showRetry = true
//                    }
//                )
//            }
//        }
//    }
}


private fun startAuthentication(
    activity: androidx.fragment.app.FragmentActivity,
    ctx: android.content.Context,
    settings: LockSettings,
    scope: kotlinx.coroutines.CoroutineScope,
    onSuccess: () -> Unit,
    onError: () -> Unit
) {
    if (!BiometricManagerHelper.canAuthenticate(
            activity,
            useBiometrics = settings.useBiometrics,
            useDeviceCredential = settings.useDeviceCredential
        )
    ) {
        onError()
        return
    }

    BiometricManagerHelper.authenticateUser(
        activity,
        useBiometrics = settings.useBiometrics,
        useDeviceCredential = settings.useDeviceCredential,
        onSuccess = {
            scope.launch {
                LockSettingsStore.updateLockSettings(
                    ctx,
                    settings.copy(lastUnlockTimestamp = Instant.now().toEpochMilli())
                )
            }
            onSuccess()
        },
        onFailure = { onError() }
    )
}
