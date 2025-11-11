package org.elnix.notes.data.settings.stores

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.elnix.notes.data.LockSettings
import org.elnix.notes.data.settings.TimeoutOptions
import org.elnix.notes.data.settings.dataStore


object LockSettingsStore {
    private val USE_BIOMETRICS = booleanPreferencesKey("use_biometrics")
    private val USE_DEVICE_CREDENTIAL = booleanPreferencesKey("use_device_credential")
    private val LOCK_TIMEOUT_SECONDS = intPreferencesKey("lock_timeout_seconds")
    private val LAST_UNLOCK_TIMESTAMP = longPreferencesKey("last_unlock_timestamp")

    fun getLockSettings(context: Context): Flow<LockSettings> {
        return context.dataStore.data.map { prefs ->
            LockSettings(
                useBiometrics = prefs[USE_BIOMETRICS] ?: false,
                useDeviceCredential = prefs[USE_DEVICE_CREDENTIAL] ?: false,
                lockTimeoutSeconds = prefs[LOCK_TIMEOUT_SECONDS] ?: 300,
                lastUnlockTimestamp = prefs[LAST_UNLOCK_TIMESTAMP] ?: 0L
            )
        }
    }

    suspend fun updateLockSettings(context: Context, settings: LockSettings) {
        Log.d("LockSettingsStore", "Saving: $settings")
        context.dataStore.edit { prefs ->
            prefs[USE_BIOMETRICS] = settings.useBiometrics
            prefs[USE_DEVICE_CREDENTIAL] = settings.useDeviceCredential
            prefs[LOCK_TIMEOUT_SECONDS] = settings.lockTimeoutSeconds
            prefs[LAST_UNLOCK_TIMESTAMP] = settings.lastUnlockTimestamp
        }
    }

    private val SELECTED_UNIT = stringPreferencesKey("selected_unit")

    fun getUnitSelected(ctx: Context) : Flow<TimeoutOptions> =
        ctx.dataStore.data.map { prefs ->
            prefs[SELECTED_UNIT]?.let { TimeoutOptions.valueOf(it) }
                ?: TimeoutOptions.MINUTES
        }

    suspend fun setUnitSelected(ctx: Context, state: TimeoutOptions) {
        ctx.dataStore.edit { it[SELECTED_UNIT] = state.name }
    }

    suspend fun resetAll(ctx: Context) {
        ctx.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}


