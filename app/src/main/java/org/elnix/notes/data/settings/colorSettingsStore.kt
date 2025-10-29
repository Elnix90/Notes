package org.elnix.notes.data.settings


import android.content.Context
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.map
import org.elnix.notes.ui.theme.AmoledDefault
import org.elnix.notes.ui.theme.DarkDefault
import org.elnix.notes.ui.theme.LightDefault

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


    suspend fun resetColors(ctx: Context,selectedColorCustomisationMode: ColorCustomisationMode , selectedMode: DefaultThemes) {
        when(selectedColorCustomisationMode) {
            ColorCustomisationMode.DEFAULT -> {
                when (selectedMode) {
                    DefaultThemes.LIGHT -> {
                        setPrimary(ctx, LightDefault.Primary.toArgb())
                        setOnPrimary(ctx, LightDefault.OnPrimary.toArgb())
                        setSecondary(ctx, LightDefault.Secondary.toArgb())
                        setOnSecondary(ctx, LightDefault.OnSecondary.toArgb())
                        setTertiary(ctx, LightDefault.Tertiary.toArgb())
                        setOnTertiary(ctx, LightDefault.OnTertiary.toArgb())
                        setBackground(ctx, LightDefault.Background.toArgb())
                        setOnBackground(ctx, LightDefault.OnBackground.toArgb())
                        setSurface(ctx, LightDefault.Surface.toArgb())
                        setOnSurface(ctx, LightDefault.OnSurface.toArgb())
                        setError(ctx, LightDefault.Error.toArgb())
                        setOnError(ctx, LightDefault.OnError.toArgb())
                        setOutline(ctx, LightDefault.Outline.toArgb())
                        setDelete(ctx, LightDefault.Delete.toArgb())
                        setEdit(ctx, LightDefault.Edit.toArgb())
                        setComplete(ctx, LightDefault.Complete.toArgb())
                    }
                    DefaultThemes.DARK -> {
                        setPrimary(ctx, DarkDefault.Primary.toArgb())
                        setOnPrimary(ctx, DarkDefault.OnPrimary.toArgb())
                        setSecondary(ctx, DarkDefault.Secondary.toArgb())
                        setOnSecondary(ctx, DarkDefault.OnSecondary.toArgb())
                        setTertiary(ctx, DarkDefault.Tertiary.toArgb())
                        setOnTertiary(ctx, DarkDefault.OnTertiary.toArgb())
                        setBackground(ctx, DarkDefault.Background.toArgb())
                        setOnBackground(ctx, DarkDefault.OnBackground.toArgb())
                        setSurface(ctx, DarkDefault.Surface.toArgb())
                        setOnSurface(ctx, DarkDefault.OnSurface.toArgb())
                        setError(ctx, DarkDefault.Error.toArgb())
                        setOnError(ctx, DarkDefault.OnError.toArgb())
                        setOutline(ctx, DarkDefault.Outline.toArgb())
                        setDelete(ctx, DarkDefault.Delete.toArgb())
                        setEdit(ctx, DarkDefault.Edit.toArgb())
                        setComplete(ctx, DarkDefault.Complete.toArgb())
                    }
                    DefaultThemes.AMOLED -> {
                        setPrimary(ctx, AmoledDefault.Primary.toArgb())
                        setOnPrimary(ctx, AmoledDefault.OnPrimary.toArgb())
                        setSecondary(ctx, AmoledDefault.Secondary.toArgb())
                        setOnSecondary(ctx, AmoledDefault.OnSecondary.toArgb())
                        setTertiary(ctx, AmoledDefault.Tertiary.toArgb())
                        setOnTertiary(ctx, AmoledDefault.OnTertiary.toArgb())
                        setBackground(ctx, AmoledDefault.Background.toArgb())
                        setOnBackground(ctx, AmoledDefault.OnBackground.toArgb())
                        setSurface(ctx, AmoledDefault.Surface.toArgb())
                        setOnSurface(ctx, AmoledDefault.OnSurface.toArgb())
                        setError(ctx, AmoledDefault.Error.toArgb())
                        setOnError(ctx, AmoledDefault.OnError.toArgb())
                        setOutline(ctx, AmoledDefault.Outline.toArgb())
                        setDelete(ctx, AmoledDefault.Delete.toArgb())
                        setEdit(ctx, AmoledDefault.Edit.toArgb())
                        setComplete(ctx, AmoledDefault.Complete.toArgb())
                    }
                }
            }
            ColorCustomisationMode.NORMAL, ColorCustomisationMode.ALL -> {
                setPrimary(ctx, AmoledDefault.Primary.toArgb())
                setOnPrimary(ctx, AmoledDefault.OnPrimary.toArgb())
                setSecondary(ctx, AmoledDefault.Secondary.toArgb())
                setOnSecondary(ctx, AmoledDefault.OnSecondary.toArgb())
                setTertiary(ctx, AmoledDefault.Tertiary.toArgb())
                setOnTertiary(ctx, AmoledDefault.OnTertiary.toArgb())
                setBackground(ctx, AmoledDefault.Background.toArgb())
                setOnBackground(ctx, AmoledDefault.OnBackground.toArgb())
                setSurface(ctx, AmoledDefault.Surface.toArgb())
                setOnSurface(ctx, AmoledDefault.OnSurface.toArgb())
                setError(ctx, AmoledDefault.Error.toArgb())
                setOnError(ctx, AmoledDefault.OnError.toArgb())
                setOutline(ctx, AmoledDefault.Outline.toArgb())
                setDelete(ctx, AmoledDefault.Delete.toArgb())
                setEdit(ctx, AmoledDefault.Edit.toArgb())
                setComplete(ctx, AmoledDefault.Complete.toArgb())
            }
        }
    }
}
