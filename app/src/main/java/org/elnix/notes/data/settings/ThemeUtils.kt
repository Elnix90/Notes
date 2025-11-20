package org.elnix.notes.data.settings

import android.content.Context
import org.elnix.notes.data.settings.stores.ColorSettingsStore
import org.elnix.notes.ui.theme.AmoledDefault
import org.elnix.notes.ui.theme.DarkDefault
import org.elnix.notes.ui.theme.LightDefault
import org.elnix.notes.ui.theme.ThemeColors

suspend fun applyDefaultThemeColors(ctx: Context, theme: DefaultThemes) {
    setThemeColors(ctx, getDefaultColorScheme(ctx, theme))
}

fun getDefaultColorScheme(ctx: Context, theme: DefaultThemes) = when (theme) {
        DefaultThemes.LIGHT -> LightDefault
        DefaultThemes.DARK -> DarkDefault
        DefaultThemes.AMOLED -> AmoledDefault
//        DefaultThemes.SYSTEM -> {
//            val nightModeFlags = ctx.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
//            if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
//                DarkDefault
//            } else {
//                LightDefault
//            }
//        }
    }


private suspend fun setThemeColors(ctx: Context, colors: ThemeColors) {
    ColorSettingsStore.setPrimary(ctx, colors.Primary)
    ColorSettingsStore.setOnPrimary(ctx, colors.OnPrimary)
    ColorSettingsStore.setSecondary(ctx, colors.Secondary)
    ColorSettingsStore.setOnSecondary(ctx, colors.OnSecondary)
    ColorSettingsStore.setTertiary(ctx, colors.Tertiary)
    ColorSettingsStore.setOnTertiary(ctx, colors.OnTertiary)
    ColorSettingsStore.setBackground(ctx, colors.Background)
    ColorSettingsStore.setOnBackground(ctx, colors.OnBackground)
    ColorSettingsStore.setSurface(ctx, colors.Surface)
    ColorSettingsStore.setOnSurface(ctx, colors.OnSurface)
    ColorSettingsStore.setError(ctx, colors.Error)
    ColorSettingsStore.setOnError(ctx, colors.OnError)
    ColorSettingsStore.setOutline(ctx, colors.Outline)
    ColorSettingsStore.setDelete(ctx, colors.Delete)
    ColorSettingsStore.setEdit(ctx, colors.Edit)
    ColorSettingsStore.setComplete(ctx, colors.Complete)
}
