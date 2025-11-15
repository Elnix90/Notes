
package org.elnix.notes.ui.settings.debug

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.elnix.notes.data.settings.stores.UserConfirmSettingsStore
import org.elnix.notes.ui.helpers.SwitchRow
import org.elnix.notes.ui.helpers.settings.SettingsLazyHeader

@Composable
fun UserConfirmDebugTab(ctx: Context, scope: CoroutineScope, onBack: (() -> Unit)) {

    val showNoteDeleteConfirmation by UserConfirmSettingsStore.getShowUserValidationDeleteNote(ctx).collectAsState(initial = true)
    val showMultipleDeleteConfirmation by UserConfirmSettingsStore.getShowUserValidationMultipleDeleteNote(ctx).collectAsState(initial = true)
    val showUserConfirmEnableDebug by UserConfirmSettingsStore.getShowEnableDebug(ctx).collectAsState(initial = true)

    SettingsLazyHeader(
        title = "Debug -> User Confirm",
        onBack = onBack,
        helpText = "Debug, too busy to make a translated explanation",
        onReset = null,
        resetText = null
    ) {
        item {
            SwitchRow(
                showNoteDeleteConfirmation,
                "Show delete confirmation"
            ) { checked ->
                scope.launch {
                    UserConfirmSettingsStore.setShowUserValidationDeleteNote(
                        ctx,
                        checked
                    )
                }
            }
        }

        item {
            SwitchRow(
                showMultipleDeleteConfirmation,
                "Show multiple delete confirmation"
            ) { checked ->
                scope.launch {
                    UserConfirmSettingsStore.setShowUserValidationMultipleDeleteNote(
                        ctx,
                        checked
                    )
                }
            }
        }

        item {
            SwitchRow(
                showUserConfirmEnableDebug,
                "Show enable debug confirmation"
            ) { checked ->
                scope.launch {
                    UserConfirmSettingsStore.setShowEnableDebug(
                        ctx,
                        checked
                    )
                }
            }
        }

    }
}

