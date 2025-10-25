// file: org/elnix/notes/MainActivity.kt
package org.elnix.notes

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import org.elnix.notes.data.settings.ColorSettingsStore
import org.elnix.notes.ui.NoteViewModel
import org.elnix.notes.ui.theme.NotesTheme

class MainActivity : FragmentActivity() {
    private val vm by viewModels<NoteViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val ctx = LocalContext.current

            val primary by ColorSettingsStore.getPrimaryFlow(ctx).collectAsState(initial = null)
            val onPrimary by ColorSettingsStore.getOnPrimaryFlow(ctx).collectAsState(initial = null)

            val secondary by ColorSettingsStore.getSecondaryFlow(ctx).collectAsState(initial = null)
            val onSecondary by ColorSettingsStore.getOnSecondaryFlow(ctx).collectAsState(initial = null)

            val tertiary by ColorSettingsStore.getTertiaryFlow(ctx).collectAsState(initial = null)
            val onTertiary by ColorSettingsStore.getOnTertiaryFlow(ctx).collectAsState(initial = null)

            val background by ColorSettingsStore.getBackgroundFlow(ctx).collectAsState(initial = null)
            val onBackground by ColorSettingsStore.getOnBackgroundFlow(ctx).collectAsState(initial = null)

            val surface by ColorSettingsStore.getSurfaceFlow(ctx).collectAsState(initial = null)
            val onSurface by ColorSettingsStore.getOnSurfaceFlow(ctx).collectAsState(initial = null)

            val error by ColorSettingsStore.getErrorFlow(ctx).collectAsState(initial = null)
            val onError by ColorSettingsStore.getOnErrorFlow(ctx).collectAsState(initial = null)

            val outline by ColorSettingsStore.getOutlineFlow(ctx).collectAsState(initial = null)

            val deleteColor by ColorSettingsStore.getDeleteFlow(ctx).collectAsState(initial = null)
            val editColor by ColorSettingsStore.getEditFlow(ctx).collectAsState(initial = null)
            val completeColor by ColorSettingsStore.getCompleteFlow(ctx).collectAsState(initial = null)


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
