// file: org/elnix/notes/MainActivity.kt
package org.elnix.notes

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.elnix.notes.data.helpers.NoteType
import org.elnix.notes.data.settings.stores.ColorSettingsStore
import org.elnix.notes.data.settings.stores.PrivacySettingsStore
import org.elnix.notes.data.settings.stores.UiSettingsStore
import org.elnix.notes.ui.NoteViewModel
import org.elnix.notes.ui.theme.NotesTheme

class MainActivity : AppCompatActivity() {
    private val vm by viewModels<NoteViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ProcessLifecycleOwner.get().lifecycle.addObserver(
            AppLifecycleObserver(vm)
        )
        
        // Ensure system windows layout control
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val controller = WindowInsetsControllerCompat(window, window.decorView)

        lifecycleScope.launch {
            UiSettingsStore.getFullscreen(this@MainActivity).collectLatest { enabled ->
                if (enabled) {
                    controller.hide(
                        WindowInsetsCompat.Type.statusBars() or
                                WindowInsetsCompat.Type.navigationBars()
                    )
                    controller.systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                } else {
                    controller.show(
                        WindowInsetsCompat.Type.statusBars() or
                                WindowInsetsCompat.Type.navigationBars()
                    )
                    controller.systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
                }
            }
        }

        lifecycleScope.launch {
            PrivacySettingsStore.getBlockScreenshots(this@MainActivity).collectLatest { enabled ->
                if (enabled) {
                    window.setFlags(
                        WindowManager.LayoutParams.FLAG_SECURE,
                        WindowManager.LayoutParams.FLAG_SECURE
                    )
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                }
            }
        }

        setContent {
            val ctx = LocalContext.current

            val noteId = intent?.getLongExtra("open_note_id", -1L) ?: -1L
            val noteTypeStr = intent?.getStringExtra("open_note_type") ?: NoteType.TEXT.name
            val noteType = NoteType.valueOf(noteTypeStr)

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

            val customSelect by ColorSettingsStore.getSelect(ctx).collectAsState(initial = null)
            val customNoteTypeText by ColorSettingsStore.getNoteTypeText(ctx).collectAsState(initial = null)
            val customNoteTypeChecklist by ColorSettingsStore.getNoteTypeChecklist(ctx).collectAsState(initial = null)
            val customNoteTypeDrawing by ColorSettingsStore.getNoteTypeDrawing(ctx).collectAsState(initial = null)

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
                customComplete = completeColor,
                customSelect =customSelect,
                customNoteTypeText = customNoteTypeText,
                customNoteTypeCheckList = customNoteTypeChecklist,
                customNoteTypeDrawing = customNoteTypeDrawing
            ) {
                MainApp(
                    vm = vm,
                    activity = this,
                    startNoteId = noteId.takeIf { it != -1L },
                    startNoteType = noteType
                )
            }
        }
    }


//    override fun onPause() {
//        vm.onAppBackground()
//        super.onPause()
//    }

//    override fun onStop() {
//        super.onStop()
//        vm.onAppBackground()
//    }


//    override fun onResume() {
//        super.onResume()
//        vm.onAppForeground()
//
//        lifecycleScope.launch {
//            val block = PrivacySettingsStore.getBlockScreenshots(this@MainActivity).first()
//            if (!block) {
//                window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
//            } else {
//                window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
//            }
//        }
//    }
}
