package org.elnix.notes

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import org.elnix.notes.ui.NoteViewModel

class AppLifecycleObserver(val vm: NoteViewModel) : DefaultLifecycleObserver {

    override fun onStart(owner: LifecycleOwner) {
        Log.d("LifeCycle","App went onStart")
        vm.onAppForeground()
    }

    override fun onResume(owner: LifecycleOwner) {
        Log.d("LifeCycle","App went onResume")
        vm.onAppForeground()
    }

    override fun onStop(owner: LifecycleOwner) {
        Log.d("LifeCycle","App went onStop")
        vm.onAppBackground()
    }
}
