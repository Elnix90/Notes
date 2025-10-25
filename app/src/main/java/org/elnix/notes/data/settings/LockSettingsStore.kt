package org.elnix.notes.data.settings

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.elnix.notes.data.LockSettings


object LockSettingsStore {
    private val USE_BIOMETRICS = booleanPreferencesKey("use_biometrics")
    private val USE_DEVICE_CREDENTIAL = booleanPreferencesKey("use_device_credential")
    private val LOCK_TIMEOUT_MINUTES = intPreferencesKey("lock_timeout_minutes")
    private val LAST_UNLOCK_TIMESTAMP = longPreferencesKey("last_unlock_timestamp")

    fun getLockSettings(context: Context): Flow<LockSettings> {
        return context.dataStore.data.map { prefs ->
            LockSettings(
                useBiometrics = prefs[USE_BIOMETRICS] ?: false,
                useDeviceCredential = prefs[USE_DEVICE_CREDENTIAL] ?: false,
                lockTimeoutMinutes = prefs[LOCK_TIMEOUT_MINUTES] ?: 5,
                lastUnlockTimestamp = prefs[LAST_UNLOCK_TIMESTAMP] ?: 0L
            )
        }
    }

    suspend fun updateLockSettings(context: Context, settings: LockSettings) {
        Log.d("LockSettingsStore", "Saving: $settings")
        context.dataStore.edit { prefs ->
            prefs[USE_BIOMETRICS] = settings.useBiometrics
            prefs[USE_DEVICE_CREDENTIAL] = settings.useDeviceCredential
            prefs[LOCK_TIMEOUT_MINUTES] = settings.lockTimeoutMinutes
            prefs[LAST_UNLOCK_TIMESTAMP] = settings.lastUnlockTimestamp
        }
    }
}


