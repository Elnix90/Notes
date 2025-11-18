package org.elnix.notes.security

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun requireBiometricAuth(
    activity: FragmentActivity,
    useBiometrics: Boolean,
    useDeviceCredential: Boolean,
    title: String = "Unlock Notes",
    subTitle: String = "",
): Boolean = suspendCancellableCoroutine { continuation ->

    // Case: no protection enabled → auto-success (same logic as authenticateUser)
    if (!useBiometrics && !useDeviceCredential) {
        continuation.resume(true)
        return@suspendCancellableCoroutine
    }

    // Check authenticator availability (same logic as canAuthenticate)
    val manager = BiometricManager.from(activity)
    val authenticators = when {
        useBiometrics && useDeviceCredential ->
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL

        useBiometrics ->
            BiometricManager.Authenticators.BIOMETRIC_STRONG

        useDeviceCredential ->
            BiometricManager.Authenticators.DEVICE_CREDENTIAL

        else -> 0
    }

    if (manager.canAuthenticate(authenticators) != BiometricManager.BIOMETRIC_SUCCESS) {
        continuation.resume(false)
        return@suspendCancellableCoroutine
    }

    val executor = ContextCompat.getMainExecutor(activity)

    // Same PromptInfo logic as your authenticateUser()
    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle(title)
        .setSubtitle(subTitle)
        .apply {
            when {
                useBiometrics && useDeviceCredential -> {
                    setAllowedAuthenticators(
                        BiometricManager.Authenticators.BIOMETRIC_STRONG or
                                BiometricManager.Authenticators.DEVICE_CREDENTIAL
                    )
                }

                useBiometrics -> {
                    setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                    setNegativeButtonText("Cancel")
                }

                useDeviceCredential -> {
                    setAllowedAuthenticators(BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                }
            }
        }
        .build()

    val biometricPrompt = BiometricPrompt(
        activity,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                if (continuation.isActive) continuation.resume(true)
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                if (!continuation.isActive) return

                // User pressed cancel OR system canceled the prompt
                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON ||
                    errorCode == BiometricPrompt.ERROR_USER_CANCELED ||
                    errorCode == BiometricPrompt.ERROR_CANCELED
                ) {
                    continuation.resumeWithException(
                        CancellationException(errString.toString())
                    )
                } else {
                    continuation.resume(false)
                }
            }

            override fun onAuthenticationFailed() {
//                super.onAuthenticationFailed()
//                // Follow your existing behavior: failed attempt = overall failure
//                if (continuation.isActive) continuation.resume(false)
            }
        }
    )

    try {
        biometricPrompt.authenticate(promptInfo)
    } catch (e: Exception) {
        e.printStackTrace()
        if (continuation.isActive) continuation.resume(false)
    }

    // Coroutine auto-cancels biometric prompt if needed (optional improvement)
}
