package org.elnix.notes.data

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow
import org.elnix.notes.ui.theme.Purple40

private val Context.dataStore by preferencesDataStore(name = "settings")

object SettingsStore {

    private val TEXT_COLOR = intPreferencesKey("text_color")
    private val PRIMARY_COLOR = intPreferencesKey("primary_color")
    private val BACKGROUND_COLOR = intPreferencesKey("background_color")

    fun getOnBackgroundFlow(ctx: Context): Flow<Int?> =
        ctx.dataStore.data.map { it[TEXT_COLOR] }

    suspend fun setOnBackground(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[TEXT_COLOR] = color }
    }

    fun getPrimaryFlow(ctx: Context): Flow<Int?> =
        ctx.dataStore.data.map { it[PRIMARY_COLOR] }

    suspend fun setPrimary(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[PRIMARY_COLOR] = color }
    }

    fun getBackgroundFlow(ctx: Context): Flow<Int?> =
        ctx.dataStore.data.map { it[BACKGROUND_COLOR] }

    suspend fun setBackground(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[BACKGROUND_COLOR] = color }
    }

    suspend fun resetColors(ctx: Context) {
        setPrimary(ctx, Purple40.toArgb())
        setBackground(ctx, Color.Black.toArgb())
        setOnBackground(ctx, Color.White.toArgb())
    }
}
