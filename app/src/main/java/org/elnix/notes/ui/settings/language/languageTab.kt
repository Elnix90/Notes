package org.elnix.notes.ui.settings.language

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.notes.R
import org.elnix.notes.data.settings.stores.LanguageSettingsStore
import org.elnix.notes.ui.helpers.SettingsTitle

@Composable
fun LanguageTab(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Available languages
    val availableLanguages = listOf(
        "en" to stringResource(R.string.language_english),
        "fr" to stringResource(R.string.language_french),
    )

    var selectedTag by remember { mutableStateOf<String?>(null) }

    // Load current language tag
    LaunchedEffect(Unit) {
        selectedTag = LanguageSettingsStore().getLanguageTag(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SettingsTitle(title = stringResource(R.string.settings_language_title), onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            availableLanguages.forEach { (tag, name) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            scope.launch {
                                LanguageSettingsStore().setLanguageTag(context, tag)
                                applyLocale(tag)
                                selectedTag = tag
                            }
                        }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = tag == selectedTag,
                        onClick = {
                            scope.launch {
                                LanguageSettingsStore().setLanguageTag(context, tag)
                                applyLocale(tag)
                                selectedTag = tag
                            }
                        }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(name)
                }
            }

            // Add option for "System default"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        scope.launch {
                            LanguageSettingsStore().setLanguageTag(context, null)
                            applyLocale(null)
                            selectedTag = null
                        }
                    }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedTag == null,
                    onClick = {
                        scope.launch {
                            LanguageSettingsStore().setLanguageTag(context, null)
                            applyLocale(null)
                            selectedTag = null
                        }
                    }
                )
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.system_default))
            }
        }
    }

}



private fun applyLocale(tag: String?) {
    val localeList = if (tag == null) {
        AppCompatDelegate.getApplicationLocales().apply {
            AppCompatDelegate.setApplicationLocales(androidx.core.os.LocaleListCompat.getEmptyLocaleList())
        }
        androidx.core.os.LocaleListCompat.getEmptyLocaleList()
    } else {
        androidx.core.os.LocaleListCompat.forLanguageTags(tag)
    }
    AppCompatDelegate.setApplicationLocales(localeList)
}
