package org.elnix.notes.security

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.util.concurrent.Executor

object BiometricManagerHelper {


    fun canAuthenticate(
        activity: FragmentActivity,
        useBiometrics: Boolean,
        useDeviceCredential: Boolean
    ): Boolean {
        if (!useBiometrics && !useDeviceCredential) {
            return false
        }
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
        return manager.canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS
    }


    fun authenticateUser(
        activity: FragmentActivity,
        useBiometrics: Boolean,
        useDeviceCredential: Boolean,
        title: String = "Unlock Notes",
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        if (!useBiometrics && !useDeviceCredential) {
            onSuccess()
            return
        }

        val executor: Executor = ContextCompat.getMainExecutor(activity)

        val allowedAuthenticators = when {
            useBiometrics && useDeviceCredential ->
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
            useBiometrics ->
                BiometricManager.Authenticators.BIOMETRIC_STRONG
            useDeviceCredential ->
                BiometricManager.Authenticators.DEVICE_CREDENTIAL
            else -> 0
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle("")
            .setAllowedAuthenticators(allowedAuthenticators)
            .apply {
                if (useBiometrics && !useDeviceCredential) {
                    setNegativeButtonText("Cancel")
                }
            }
            .build()

        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onFailure()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onFailure()
                }
            }
        )

        biometricPrompt.authenticate(promptInfo)
    }
}
