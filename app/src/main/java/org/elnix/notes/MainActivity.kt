// file: org/elnix/notes/MainActivity.kt
package org.elnix.notes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import org.elnix.notes.data.SettingsStore
import org.elnix.notes.ui.NoteViewModel
import org.elnix.notes.ui.theme.NotesTheme

class MainActivity : ComponentActivity() {
    private val vm by viewModels<NoteViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val ctx = LocalContext.current

            val primary by SettingsStore.getPrimaryFlow(ctx).collectAsState(initial = null)
            val onPrimary by SettingsStore.getOnPrimaryFlow(ctx).collectAsState(initial = null)

            val secondary by SettingsStore.getSecondaryFlow(ctx).collectAsState(initial = null)
            val onSecondary by SettingsStore.getOnSecondaryFlow(ctx).collectAsState(initial = null)

            val tertiary by SettingsStore.getTertiaryFlow(ctx).collectAsState(initial = null)
            val onTertiary by SettingsStore.getOnTertiaryFlow(ctx).collectAsState(initial = null)

            val background by SettingsStore.getBackgroundFlow(ctx).collectAsState(initial = null)
            val onBackground by SettingsStore.getOnBackgroundFlow(ctx).collectAsState(initial = null)

            val surface by SettingsStore.getSurfaceFlow(ctx).collectAsState(initial = null)
            val onSurface by SettingsStore.getOnSurfaceFlow(ctx).collectAsState(initial = null)

            val error by SettingsStore.getErrorFlow(ctx).collectAsState(initial = null)
            val onError by SettingsStore.getOnErrorFlow(ctx).collectAsState(initial = null)

            val outline by SettingsStore.getOutlineFlow(ctx).collectAsState(initial = null)

            val deleteColor by SettingsStore.getDeleteFlow(ctx).collectAsState(initial = null)
            val editColor by SettingsStore.getEditFlow(ctx).collectAsState(initial = null)
            val completeColor by SettingsStore.getCompleteFlow(ctx).collectAsState(initial = null)


            NotesTheme(
                customPrimary = primary,
                customOnPrimary = onPrimary,
                customSecondary = secondary,
                customOnSecondary = onSecondary,
                customTertiary = tertiary,
                customOnTertiary = onTertiary,
                customBackground = background,
                customOnBackground = onBackground,
                customSurface = surface,
                customOnSurface = onSurface,
                customError = error,
                customOnError = onError,
                customOutline = outline,
                customDelete = deleteColor,
                customEdit = editColor,
                customComplete = completeColor
            ) {
                MainApp(vm)
            }
        }


    }
}
