package org.elnix.notes.data

data class LockSettings(
    val useBiometrics: Boolean = false,
    val useDeviceCredential: Boolean = false,
    val lockTimeoutSeconds: Int = 5,
    val lastUnlockTimestamp: Long = 0L
)