package org.elnix.notes.data

data class LockSettings(
    val useBiometrics: Boolean = false,
    val useDeviceCredential: Boolean = false,
    val lockTimeoutMinutes: Int = 5,
    val lastUnlockTimestamp: Long = 0L
)