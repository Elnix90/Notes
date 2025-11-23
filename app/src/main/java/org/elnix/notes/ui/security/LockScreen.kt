package org.elnix.notes.ui.security

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.launch
import org.elnix.notes.R
import org.elnix.notes.data.LockSettings
import org.elnix.notes.data.settings.stores.LockSettingsStore
import org.elnix.notes.security.BiometricManagerHelper
import org.elnix.notes.ui.theme.AppObjectsColors
import java.time.Instant

@Composable
fun LockScreen(activity: FragmentActivity, onUnlock: () -> Unit) {
    val ctx = LocalContext.current

    val scope = rememberCoroutineScope()
    val settingsFlow = LockSettingsStore.getLockSettings(ctx)
    val settings by settingsFlow.collectAsState(initial = null)

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
                onFailure = { authenticationStarted = false }
            )
        }
    }


    // Trigger authentication when the app resumes from background
    DisposableEffect(activity) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (settings != null && !authenticationStarted) {
                    authenticationStarted = true
                    startAuthentication(
                        activity,
                        ctx,
                        settings!!,
                        scope,
                        onSuccess = onUnlock,
                        onFailure = { authenticationStarted = false }
                    )
                }
            }
        }
        activity.lifecycle.addObserver(observer)
        onDispose {
            activity.lifecycle.removeObserver(observer)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                enabled = settings != null,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                startAuthentication(
                    activity,
                    ctx,
                    settings!!,
                    scope,
                    onSuccess = onUnlock,
                    onFailure = { }
                )
            }
            .background(MaterialTheme.colorScheme.background)
            .padding(15.dp)
            .imePadding()
            .padding(WindowInsets.systemBars.asPaddingValues()),
        contentAlignment = Alignment.Center
    ) {


        Box(
            modifier = Modifier
                .size(300.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = "App icon",
                modifier = Modifier.fillMaxSize()
            )
        }


        Button(
            onClick = {
                if ( settings != null ) {
                    startAuthentication(
                        activity,
                        ctx,
                        settings!!,
                        scope,
                        onSuccess = onUnlock,
                        onFailure = { }
                    )
                }
            },
            colors = AppObjectsColors.buttonColors(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text(stringResource(R.string.authenticate))
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
        else (now - settings.lastUnlockTimestamp) / 1000
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
            title = ctx.getString(R.string.unlock_notes),
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
