// file: org/elnix/notes/MainActivity.kt
package org.elnix.notes

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import org.elnix.notes.data.settings.ColorSettingsStore
import org.elnix.notes.ui.NoteViewModel
import org.elnix.notes.ui.theme.NotesTheme

class MainActivity : AppCompatActivity() {
    private val vm by viewModels<NoteViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val ctx = LocalContext.current

            val primary by ColorSettingsStore.getPrimary(ctx).collectAsState(initial = null)
            val onPrimary by ColorSettingsStore.getOnPrimary(ctx).collectAsState(initial = null)

            val secondary by ColorSettingsStore.getSecondary(ctx).collectAsState(initial = null)
            val onSecondary by ColorSettingsStore.getOnSecondary(ctx).collectAsState(initial = null)

            val tertiary by ColorSettingsStore.getTertiary(ctx).collectAsState(initial = null)
            val onTertiary by ColorSettingsStore.getOnTertiary(ctx).collectAsState(initial = null)

            val background by ColorSettingsStore.getBackground(ctx).collectAsState(initial = null)
            val onBackground by ColorSettingsStore.getOnBackground(ctx).collectAsState(initial = null)

            val surface by ColorSettingsStore.getSurface(ctx).collectAsState(initial = null)
            val onSurface by ColorSettingsStore.getOnSurface(ctx).collectAsState(initial = null)

            val error by ColorSettingsStore.getError(ctx).collectAsState(initial = null)
            val onError by ColorSettingsStore.getOnError(ctx).collectAsState(initial = null)

            val outline by ColorSettingsStore.getOutline(ctx).collectAsState(initial = null)

            val deleteColor by ColorSettingsStore.getDelete(ctx).collectAsState(initial = null)
            val editColor by ColorSettingsStore.getEdit(ctx).collectAsState(initial = null)
            val completeColor by ColorSettingsStore.getComplete(ctx).collectAsState(initial = null)


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
                MainApp(vm, this)
            }
        }
    }
}
