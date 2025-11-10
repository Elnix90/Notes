
package org.elnix.notes.ui.settings.debug

import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.elnix.notes.data.settings.stores.UserConfirmSettingsStore
import org.elnix.notes.ui.settings.SettingsLazyHeader
import org.elnix.notes.ui.theme.AppObjectsColors

@Composable
fun UserConfirmDebugTab(ctx: Context, scope: CoroutineScope, onBack: (() -> Unit)) {

    val showNoteDeleteConfirmation by UserConfirmSettingsStore.getShowUserValidationDeleteNote(ctx).collectAsState(initial = true)
    val showMultipleDeleteConfirmation by UserConfirmSettingsStore.getShowUserValidationMultipleDeleteNote(ctx).collectAsState(initial = true)
    val showMultipleEditConfirmation by UserConfirmSettingsStore.getShowUserValidationEditMultipleNote(ctx).collectAsState(initial = true)
    val showUserConfirmEnableDebug by UserConfirmSettingsStore.getShowEnableDebug(ctx).collectAsState(initial = true)

    SettingsLazyHeader(
        title = "Debug -> User Confirm",
        onBack = onBack
    ) {
        item {
            Button(
                onClick = {
                    scope.launch {
                        UserConfirmSettingsStore.setShowUserValidationDeleteNote(
                            ctx,
                            !showNoteDeleteConfirmation
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = AppObjectsColors.buttonColors()
            ) {
                Text(
                    text = "Show delete confirm : $showNoteDeleteConfirmation",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        item {
            Button(
                onClick = {
                    scope.launch {
                        UserConfirmSettingsStore.setShowUserValidationMultipleDeleteNote(
                            ctx,
                            !showMultipleDeleteConfirmation
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = AppObjectsColors.buttonColors()
            ) {
                Text(
                    text = "Show multiple delete confirm : $showMultipleDeleteConfirmation",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        item {
            Button(
                onClick = {
                    scope.launch {
                        UserConfirmSettingsStore.setShowUserValidationEditMultipleNote(
                            ctx,
                            !showMultipleEditConfirmation
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = AppObjectsColors.buttonColors()
            ) {
                Text(
                    text = "Show multiple edit confirm : $showMultipleEditConfirmation",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        item {
            Button(
                onClick = {
                    scope.launch {
                        UserConfirmSettingsStore.setShowEnableDebug(
                            ctx,
                            !showUserConfirmEnableDebug
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = AppObjectsColors.buttonColors()
            ) {
                Text(
                    text = "Show enable debug confirm : $showUserConfirmEnableDebug",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

