package org.elnix.notes.data.settings.stores

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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
            prefs.remove(USE_BIOMETRICS)
            prefs.remove(USE_DEVICE_CREDENTIAL)
            prefs.remove(LOCK_TIMEOUT_SECONDS)
            prefs.remove(LAST_UNLOCK_TIMESTAMP)
        }
    }

    suspend fun getAll(ctx: Context): Map<String, String> {
        val prefs = ctx.dataStore.data.first()
        return buildMap {
            prefs[USE_BIOMETRICS]?.let { put(USE_BIOMETRICS.name, it.toString()) }
            prefs[USE_DEVICE_CREDENTIAL]?.let { put(USE_DEVICE_CREDENTIAL.name, it.toString()) }
            prefs[LOCK_TIMEOUT_SECONDS]?.let { put(LOCK_TIMEOUT_SECONDS.name, it.toString()) }
            prefs[LAST_UNLOCK_TIMESTAMP]?.let { put(LAST_UNLOCK_TIMESTAMP.name, it.toString()) }
            prefs[SELECTED_UNIT]?.let { put(SELECTED_UNIT.name, it) }
        }
    }

    suspend fun setAll(ctx: Context, data: Map<String, String>) {
        ctx.dataStore.edit { prefs ->
            data[USE_BIOMETRICS.name]?.let { prefs[USE_BIOMETRICS] = it.toBoolean() }
            data[USE_DEVICE_CREDENTIAL.name]?.let { prefs[USE_DEVICE_CREDENTIAL] = it.toBoolean() }
            data[LOCK_TIMEOUT_SECONDS.name]?.let { prefs[LOCK_TIMEOUT_SECONDS] = it.toIntOrNull() ?: 300 }
            data[LAST_UNLOCK_TIMESTAMP.name]?.let { prefs[LAST_UNLOCK_TIMESTAMP] = it.toLongOrNull() ?: 0L }
            data[SELECTED_UNIT.name]?.let { prefs[SELECTED_UNIT] = it }
        }
    }
}
