// file: org/elnix/notes/MyApplication.kt
package org.elnix.notes

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.elnix.notes.data.settings.stores.LanguageSettingsStore

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Re-apply saved app language early
        val store = LanguageSettingsStore()
        CoroutineScope(Dispatchers.Default).launch {
            val tag = store.getLanguageTag(this@MyApplication)
            if (!tag.isNullOrEmpty()) {
                AppCompatDelegate.setApplicationLocales(
                    LocaleListCompat.forLanguageTags(tag)
                )
            }
        }
    }
}
