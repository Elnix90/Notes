package org.elnix.notes.data.settings.stores


import android.content.Context
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.map
import org.elnix.notes.data.settings.ColorCustomisationMode
import org.elnix.notes.data.settings.DefaultThemes
import org.elnix.notes.data.settings.dataStore
import org.elnix.notes.data.settings.stores.ColorSettingsStore.setBackground
import org.elnix.notes.data.settings.stores.ColorSettingsStore.setComplete
import org.elnix.notes.data.settings.stores.ColorSettingsStore.setDelete
import org.elnix.notes.data.settings.stores.ColorSettingsStore.setEdit
import org.elnix.notes.data.settings.stores.ColorSettingsStore.setError
import org.elnix.notes.data.settings.stores.ColorSettingsStore.setOnBackground
import org.elnix.notes.data.settings.stores.ColorSettingsStore.setOnError
import org.elnix.notes.data.settings.stores.ColorSettingsStore.setOnPrimary
import org.elnix.notes.data.settings.stores.ColorSettingsStore.setOnSecondary
import org.elnix.notes.data.settings.stores.ColorSettingsStore.setOnSurface
import org.elnix.notes.data.settings.stores.ColorSettingsStore.setOnTertiary
import org.elnix.notes.data.settings.stores.ColorSettingsStore.setOutline
import org.elnix.notes.data.settings.stores.ColorSettingsStore.setPrimary
import org.elnix.notes.data.settings.stores.ColorSettingsStore.setSecondary
import org.elnix.notes.data.settings.stores.ColorSettingsStore.setSurface
import org.elnix.notes.data.settings.stores.ColorSettingsStore.setTertiary
import org.elnix.notes.ui.theme.AmoledDefault
import org.elnix.notes.ui.theme.DarkDefault
import org.elnix.notes.ui.theme.LightDefault
import org.elnix.notes.ui.theme.ThemeColors

object ColorSettingsStore {

    private val PRIMARY_COLOR = intPreferencesKey("primary_color")
    private val ON_PRIMARY_COLOR = intPreferencesKey("on_primary_color")
    private val SECONDARY_COLOR = intPreferencesKey("secondary_color")
    private val ON_SECONDARY_COLOR = intPreferencesKey("on_secondary_color")
    private val TERTIARY_COLOR = intPreferencesKey("tertiary_color")
    private val ON_TERTIARY_COLOR = intPreferencesKey("on_tertiary_color")
    private val BACKGROUND_COLOR = intPreferencesKey("background_color")
    private val ON_BACKGROUND_COLOR = intPreferencesKey("on_background_color")

    private val SURFACE_COLOR = intPreferencesKey("surface_color")
    private val ON_SURFACE_COLOR = intPreferencesKey("on_surface_color")
    private val ERROR_COLOR = intPreferencesKey("error_color")
    private val ON_ERROR_COLOR = intPreferencesKey("on_error_color")

    private val OUTLINE_COLOR = intPreferencesKey("outline_color")

    private val DELETE_COLOR = intPreferencesKey("delete_color")
    private val EDIT_COLOR = intPreferencesKey("edit_color")
    private val COMPLETE_COLOR = intPreferencesKey("complete_color")





    fun getPrimary(ctx: Context) = ctx.dataStore.data.map { it[PRIMARY_COLOR] }
    suspend fun setPrimary(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[PRIMARY_COLOR] = color }
    }

    fun getOnPrimary(ctx: Context) = ctx.dataStore.data.map { it[ON_PRIMARY_COLOR] }
    suspend fun setOnPrimary(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[ON_PRIMARY_COLOR] = color }
    }

    fun getSecondary(ctx: Context) = ctx.dataStore.data.map { it[SECONDARY_COLOR] }
    suspend fun setSecondary(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[SECONDARY_COLOR] = color }
    }

    fun getOnSecondary(ctx: Context) = ctx.dataStore.data.map { it[ON_SECONDARY_COLOR] }
    suspend fun setOnSecondary(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[ON_SECONDARY_COLOR] = color }
    }

    fun getTertiary(ctx: Context) = ctx.dataStore.data.map { it[TERTIARY_COLOR] }
    suspend fun setTertiary(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[TERTIARY_COLOR] = color }
    }

    fun getOnTertiary(ctx: Context) = ctx.dataStore.data.map { it[ON_TERTIARY_COLOR] }
    suspend fun setOnTertiary(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[ON_TERTIARY_COLOR] = color }
    }


    fun getBackground(ctx: Context) = ctx.dataStore.data.map { it[BACKGROUND_COLOR] }
    suspend fun setBackground(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[BACKGROUND_COLOR] = color }
    }

    fun getOnBackground(ctx: Context) = ctx.dataStore.data.map { it[ON_BACKGROUND_COLOR] }
    suspend fun setOnBackground(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[ON_BACKGROUND_COLOR] = color }
    }


    fun getSurface(ctx: Context) = ctx.dataStore.data.map { it[SURFACE_COLOR] }
    suspend fun setSurface(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[SURFACE_COLOR] = color }
    }

    fun getOnSurface(ctx: Context) = ctx.dataStore.data.map { it[ON_SURFACE_COLOR] }
    suspend fun setOnSurface(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[ON_SURFACE_COLOR] = color }
    }

    fun getError(ctx: Context) = ctx.dataStore.data.map { it[ERROR_COLOR] }
    suspend fun setError(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[ERROR_COLOR] = color }
    }

    fun getOnError(ctx: Context) = ctx.dataStore.data.map { it[ON_ERROR_COLOR] }
    suspend fun setOnError(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[ON_ERROR_COLOR] = color }
    }

    fun getOutline(ctx: Context) = ctx.dataStore.data.map { it[OUTLINE_COLOR] }
    suspend fun setOutline(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[OUTLINE_COLOR] = color }
    }

    fun getDelete(ctx: Context) = ctx.dataStore.data.map { it[DELETE_COLOR] }
    suspend fun setDelete(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[DELETE_COLOR] = color }
    }

    fun getEdit(ctx: Context) = ctx.dataStore.data.map { it[EDIT_COLOR] }
    suspend fun setEdit(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[EDIT_COLOR] = color }
    }

    fun getComplete(ctx: Context) = ctx.dataStore.data.map { it[COMPLETE_COLOR] }
    suspend fun setComplete(ctx: Context, color: Int) {
        ctx.dataStore.edit { it[COMPLETE_COLOR] = color }
    }

    suspend fun resetColors(
        ctx: Context,
        selectedColorCustomisationMode: ColorCustomisationMode,
        selectedMode: DefaultThemes
    ) {

        val themeColors: ThemeColors = when (selectedColorCustomisationMode) {
            ColorCustomisationMode.DEFAULT -> when (selectedMode) {
                DefaultThemes.LIGHT -> LightDefault
                DefaultThemes.DARK -> DarkDefault
                DefaultThemes.AMOLED -> AmoledDefault
            }
            ColorCustomisationMode.NORMAL,
            ColorCustomisationMode.ALL -> AmoledDefault
        }

        applyThemeColors(ctx, themeColors)
    }
}


private suspend fun applyThemeColors(ctx: Context, colors: ThemeColors) {
    setPrimary(ctx, colors.Primary.toArgb())
    setOnPrimary(ctx, colors.OnPrimary.toArgb())
    setSecondary(ctx, colors.Secondary.toArgb())
    setOnSecondary(ctx, colors.OnSecondary.toArgb())
    setTertiary(ctx, colors.Tertiary.toArgb())
    setOnTertiary(ctx, colors.OnTertiary.toArgb())
    setBackground(ctx, colors.Background.toArgb())
    setOnBackground(ctx, colors.OnBackground.toArgb())
    setSurface(ctx, colors.Surface.toArgb())
    setOnSurface(ctx, colors.OnSurface.toArgb())
    setError(ctx, colors.Error.toArgb())
    setOnError(ctx, colors.OnError.toArgb())
    setOutline(ctx, colors.Outline.toArgb())
    setDelete(ctx, colors.Delete.toArgb())
    setEdit(ctx, colors.Edit.toArgb())
    setComplete(ctx, colors.Complete.toArgb())
}
