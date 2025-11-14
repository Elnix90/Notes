package org.elnix.notes.ui.settings.language

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.notes.R
import org.elnix.notes.data.settings.stores.LanguageSettingsStore
import org.elnix.notes.ui.helpers.settings.SettingsLazyHeader
import org.elnix.notes.ui.theme.AppObjectsColors

@Composable
fun LanguageTab(onBack: () -> Unit) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    // Available languages
    val availableLanguages = listOf(
        "en" to stringResource(R.string.language_english),
        "fr" to stringResource(R.string.language_french),
        null to stringResource(R.string.system_default)
    )

    var selectedTag by remember { mutableStateOf<String?>(null) }

    // Load current language tag
    LaunchedEffect(Unit) {
        selectedTag = LanguageSettingsStore.getLanguageTag(ctx)
    }

    SettingsLazyHeader(
        title = stringResource(R.string.settings_language_title),
        onBack = onBack,
        helpText = stringResource(R.string.choose_your_app_language),
        onReset = {
            scope.launch {
                LanguageSettingsStore.resetAll(ctx)
            }
        }
    ) {

        items(availableLanguages) { (tag, name) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(5.dp)
                    .clickable {
                        scope.launch {
                            LanguageSettingsStore.setLanguageTag(ctx, tag)
                            applyLocale(tag)
                            selectedTag = tag
                        }
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = tag == selectedTag,
                    onClick = {
                        scope.launch {
                            LanguageSettingsStore.setLanguageTag(ctx, tag)
                            applyLocale(tag)
                            selectedTag = tag
                        }
                    },
                    colors = AppObjectsColors.radioButtonColors()
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = name,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Add option for "System default"
//        item {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .clip(RoundedCornerShape(12.dp))
//                    .background(MaterialTheme.colorScheme.surface)
//                    .clickable {
//                        scope.launch {
//                            LanguageSettingsStore.setLanguageTag(ctx, null)
//                            applyLocale(null)
//                            selectedTag = null
//                        }
//                    },
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                RadioButton(
//                    selected = selectedTag == null,
//                    onClick = {
//                        scope.launch {
//                            LanguageSettingsStore.setLanguageTag(ctx, null)
//                            applyLocale(null)
//                            selectedTag = null
//                        }
//                    }
//                )
//                Spacer(Modifier.width(8.dp))
//                Text(stringResource(R.string.system_default))
//            }
//        }
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
