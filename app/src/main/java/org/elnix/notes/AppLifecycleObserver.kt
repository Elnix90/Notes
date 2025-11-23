package org.elnix.notes

import android.view.WindowManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.elnix.notes.data.settings.stores.PrivacySettingsStore
import org.elnix.notes.ui.NoteViewModel

class AppLifecycleObserver(val vm: NoteViewModel) : DefaultLifecycleObserver {

    override fun onStart(owner: LifecycleOwner) {
        vm.onAppForeground()

        lifecycleScope.launch {
            val block = PrivacySettingsStore.getBlockScreenshots(this@MainActivity).first()
            if (!block) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
            } else {
                window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
            }
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        vm.onAppBackground()
    }
}
