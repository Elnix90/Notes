package org.elnix.notes.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.burnoutcrew.reorderable.ReorderableLazyListState
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.reorderable
import org.elnix.notes.R
import org.elnix.notes.ui.helpers.UserValidation
import org.elnix.notes.ui.helpers.settings.SettingsTitle

@Composable
fun SettingsLazyHeader(
    title: String,
    onBack: () -> Unit,
    helpText: String,
    onReset: (() -> Unit)?,
    modifier: Modifier = Modifier,
    resetTitle: String = stringResource(R.string.reset_default_settings),
    resetText: String? = stringResource(R.string.reset_settings_in_this_tab),
    reorderState: ReorderableLazyListState? = null,
    content: LazyListScope.() -> Unit
) {

    var showHelpDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(WindowInsets.systemBars.asPaddingValues())
            .padding(horizontal = 16.dp)
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(color = MaterialTheme.colorScheme.background) {
            SettingsTitle(
                title,
                helpIcon = { showHelpDialog = true },
                resetIcon = if (onReset != null) {
                    { showResetDialog = true }
                } else null,
            ) { onBack() }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(bottom = 400.dp),
            modifier = if (reorderState != null) {
                modifier
                    .reorderable(reorderState)
                    .detectReorderAfterLongPress(reorderState)
            } else modifier,
            state = reorderState?.listState ?: rememberLazyListState()
        ) {
            content()
        }
    }

    if (showHelpDialog) {
        UserValidation(
            title = "$title ${stringResource(R.string.help)}",
            message = helpText,
            cancelText = null,
            onCancel = { showHelpDialog = false },
            titleIcon = Icons.AutoMirrored.Filled.Help,
            titleColor = MaterialTheme.colorScheme.onSurface
        ) {
            showHelpDialog = false
        }
    }
    if (showResetDialog && resetText != null && onReset != null) {
        UserValidation(
            title = resetTitle,
            message = resetText,
            onCancel = { showResetDialog = false }
        ) {
            onReset()
            showResetDialog = false
        }
    }
}
