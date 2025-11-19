
package org.elnix.notes.ui.settings.debug

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.elnix.notes.data.settings.stores.UserConfirmEntry
import org.elnix.notes.data.settings.stores.UserConfirmSettingsStore
import org.elnix.notes.ui.helpers.SwitchRow
import org.elnix.notes.ui.helpers.settings.SettingsLazyHeader

@Composable
fun UserConfirmDebugTab(ctx: Context, scope: CoroutineScope, onBack: (() -> Unit)) {

    val showNoteDeleteConfirmation by UserConfirmSettingsStore.get(
        ctx = ctx,
        entry = UserConfirmEntry.SHOW_USER_VALIDATION_DELETE_NOTE
    ).collectAsState(initial = true)
    val showMultipleDeleteConfirmation by UserConfirmSettingsStore.get(
        ctx = ctx,
        entry = UserConfirmEntry.SHOW_USER_VALIDATION_MULTIPLE_DELETE_NOTE
    ).collectAsState(initial = true)
    val showUserConfirmEnableDebug by UserConfirmSettingsStore.get(
        ctx = ctx,
        entry = UserConfirmEntry.SHOW_ENABLE_DEBUG
    ).collectAsState(initial = true)
    val showDeleteOffsetConfirmation by UserConfirmSettingsStore.get(
        ctx = ctx,
        entry = UserConfirmEntry.SHOW_USER_VALIDATION_DELETE_OFFSET
    ).collectAsState(initial = true)
    val showDeleteTagConfirmation by UserConfirmSettingsStore.get(
        ctx = ctx,
        entry = UserConfirmEntry.SHOW_USER_VALIDATION_DELETE_TAG
    ).collectAsState(initial = true)


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
                    UserConfirmSettingsStore.set(
                        ctx = ctx,
                        entry = UserConfirmEntry.SHOW_USER_VALIDATION_DELETE_NOTE,
                        value = checked
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
                    UserConfirmSettingsStore.set(
                        ctx = ctx,
                        entry = UserConfirmEntry.SHOW_USER_VALIDATION_MULTIPLE_DELETE_NOTE,
                        value = checked
                    )
                }
            }
        }

        item {
            SwitchRow(
                showDeleteOffsetConfirmation,
                "Show delete offset confirmation"
            ) { checked ->
                scope.launch {
                    UserConfirmSettingsStore.set(
                        ctx = ctx,
                        entry = UserConfirmEntry.SHOW_USER_VALIDATION_DELETE_OFFSET,
                        value = checked
                    )
                }
            }
        }

        item {
            SwitchRow(
                showDeleteTagConfirmation,
                "Show delete tag confirmation"
            ) { checked ->
                scope.launch {
                    UserConfirmSettingsStore.set(
                        ctx = ctx,
                        entry = UserConfirmEntry.SHOW_USER_VALIDATION_DELETE_TAG,
                        value = checked
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
                    UserConfirmSettingsStore.set(
                        ctx = ctx,
                        entry = UserConfirmEntry.SHOW_ENABLE_DEBUG,
                        value = checked
                    )
                }
            }
        }

    }
}

