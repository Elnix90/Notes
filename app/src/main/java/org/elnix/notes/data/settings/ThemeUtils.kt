package org.elnix.notes.data.settings

import android.content.Context
import androidx.compose.ui.graphics.toArgb
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
    ColorSettingsStore.setPrimary(ctx, colors.Primary.toArgb())
    ColorSettingsStore.setOnPrimary(ctx, colors.OnPrimary.toArgb())
    ColorSettingsStore.setSecondary(ctx, colors.Secondary.toArgb())
    ColorSettingsStore.setOnSecondary(ctx, colors.OnSecondary.toArgb())
    ColorSettingsStore.setTertiary(ctx, colors.Tertiary.toArgb())
    ColorSettingsStore.setOnTertiary(ctx, colors.OnTertiary.toArgb())
    ColorSettingsStore.setBackground(ctx, colors.Background.toArgb())
    ColorSettingsStore.setOnBackground(ctx, colors.OnBackground.toArgb())
    ColorSettingsStore.setSurface(ctx, colors.Surface.toArgb())
    ColorSettingsStore.setOnSurface(ctx, colors.OnSurface.toArgb())
    ColorSettingsStore.setError(ctx, colors.Error.toArgb())
    ColorSettingsStore.setOnError(ctx, colors.OnError.toArgb())
    ColorSettingsStore.setOutline(ctx, colors.Outline.toArgb())
    ColorSettingsStore.setDelete(ctx, colors.Delete.toArgb())
    ColorSettingsStore.setEdit(ctx, colors.Edit.toArgb())
    ColorSettingsStore.setComplete(ctx, colors.Complete.toArgb())
}
