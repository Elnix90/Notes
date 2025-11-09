
package org.elnix.notes.ui.settings.debug

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.elnix.notes.data.settings.stores.UserConfirmSettingsStore
import org.elnix.notes.ui.helpers.TextDivider
import org.elnix.notes.ui.helpers.settings.SettingsTitle
import org.elnix.notes.ui.theme.AppObjectsColors

@Composable
fun UserConfirmDebugTab(ctx: Context, scope: CoroutineScope, onBack: (() -> Unit)) {

    val showNoteDeleteConfirmation by UserConfirmSettingsStore.getShowUserValidationDeleteNote(ctx).collectAsState(initial = true)
    val showMultipleDeleteConfirmation by UserConfirmSettingsStore.getShowUserValidationMultipleDeleteNote(ctx).collectAsState(initial = true)
    val showMultipleEditConfirmation by UserConfirmSettingsStore.getShowUserValidationEditMultipleNote(ctx).collectAsState(initial = true)
    val showUserConfirmEnableDebug by UserConfirmSettingsStore.getShowEnableDebug(ctx).collectAsState(initial = true)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                WindowInsets.systemBars
                    .asPaddingValues()
            )
            .padding(horizontal = 16.dp, vertical = 5.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SettingsTitle(title = "Debug -> User Confirm", onBack = onBack)

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

            TextDivider("user confirmations")

            Button(
                onClick = {
                    scope.launch{
                        UserConfirmSettingsStore.setShowUserValidationDeleteNote(
                            ctx,
                            !showNoteDeleteConfirmation
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = AppObjectsColors.buttonColors()
            ) {
                Text(text = "Show delete confirm : $showNoteDeleteConfirmation",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Button(
                onClick = {
                    scope.launch{
                        UserConfirmSettingsStore.setShowUserValidationMultipleDeleteNote(
                            ctx,
                            !showMultipleDeleteConfirmation
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = AppObjectsColors.buttonColors()
            ) {
                Text(text = "Show multiple delete confirm : $showMultipleDeleteConfirmation",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Button(
                onClick = {
                    scope.launch{
                        UserConfirmSettingsStore.setShowUserValidationEditMultipleNote(
                            ctx,
                            !showMultipleEditConfirmation
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = AppObjectsColors.buttonColors()
            ) {
                Text(text = "Show multiple edit confirm : $showMultipleEditConfirmation",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Button(
                onClick = {
                    scope.launch{
                        UserConfirmSettingsStore.setShowEnableDebug(
                            ctx,
                            !showUserConfirmEnableDebug
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = AppObjectsColors.buttonColors()
            ) {
                Text(text = "Show enable debug confirm : $showUserConfirmEnableDebug",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

