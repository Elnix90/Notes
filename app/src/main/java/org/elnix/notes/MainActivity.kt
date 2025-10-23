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
            

            val background by SettingsStore.getBackgroundFlow(ctx).collectAsState(initial = null)
            val onBackground by SettingsStore.getOnBackgroundFlow(ctx).collectAsState(initial = null)


            NotesTheme(
                customPrimary = primary,
                customBackground = background,
                customOnBackground = onBackground
            ) {
                MainApp(vm)
            }
        }
    }
}
