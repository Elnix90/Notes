package org.elnix.notes.ui.helpers.reminders


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.launch
import org.elnix.notes.R
import org.elnix.notes.data.settings.stores.ReminderSettingsStore
import org.elnix.notes.data.settings.stores.UserConfirmEntry
import org.elnix.notes.data.settings.stores.UserConfirmSettingsStore
import org.elnix.notes.ui.helpers.StyledReminderDialogs
import org.elnix.notes.ui.helpers.UserValidation
import org.elnix.notes.ui.security.AskNotificationButton
import org.elnix.notes.ui.theme.AppObjectsColors
import org.elnix.notes.ui.theme.adjustBrightness
import org.elnix.notes.utils.ReminderOffset

@Composable
fun OffsetPickerDialog(
    offsets: List<ReminderOffset>,
    activity: FragmentActivity,
    onDismiss: () -> Unit,
    onPicked: (ReminderOffset) -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showOffsetEditor by remember { mutableStateOf(false) }
    var showReminderEditor by remember { mutableStateOf(false) }
    var editOffest by remember { mutableStateOf<ReminderOffset?>(null) }

    val lifecycleOwner = LocalLifecycleOwner.current
    var hasPermission by remember { mutableStateOf(false) }

    val showDeleteOffsetConfirmation by UserConfirmSettingsStore.get(
        ctx = ctx,
        entry = UserConfirmEntry.SHOW_USER_VALIDATION_DELETE_OFFSET
    ).collectAsState(initial = true)

    fun checkPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    LaunchedEffect(Unit) {
        hasPermission = checkPermission(ctx)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasPermission = checkPermission(ctx)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    if (!hasPermission) {
        AlertDialog(
            containerColor = MaterialTheme.colorScheme.surface,
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = stringResource(R.string.allow_notif_perm),
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.this_feature_needs_notification)
                    )
                    AskNotificationButton(activity)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = onDismiss,
                    colors = AppObjectsColors.cancelButtonColors()
                ) {
                    Text(
                        text = stringResource(R.string.cancel),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        )
    } else {
        AlertDialog(
            containerColor = MaterialTheme.colorScheme.surface,
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = stringResource(R.string.pick_a_offset),
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                        modifier = Modifier.heightIn(max = 300.dp)
                    ) {
                        items(offsets.sortedBy { it.toCalendar().timeInMillis }) { offset ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .horizontalScroll(rememberScrollState())
                                    .background(
                                        MaterialTheme.colorScheme.surface.adjustBrightness(
                                            0.7f
                                        )
                                    )
                                    .clickable { onPicked(offset) }
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TimeBubble(reminderOffset = offset)

                                // Edit + Delete buttons
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(
                                        onClick = {
                                            editOffest = offset
                                            if (offset.isAbsolute) showReminderEditor = true
                                            else showOffsetEditor = true
                                        }
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit Tag")
                                    }
                                    IconButton(
                                        onClick = {
                                            if (showDeleteOffsetConfirmation) {
                                                editOffest = offset
                                                showDeleteConfirm = true
                                            } else scope.launch { ReminderSettingsStore.deleteReminder(ctx, offset) }
                                        }
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Delete Tag",
                                            tint = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }
                            }
                        }
                    }


                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { editOffest = null; showOffsetEditor = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(R.string.create_new_offset),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                text = stringResource(R.string.create_new_offset),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        Spacer(Modifier.width(5.dp))


                        IconButton(
                            onClick = {
                                editOffest = null
                                showReminderEditor = true
                            },
                            colors = AppObjectsColors.iconButtonColors()
                        ) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = null)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = onDismiss,
                    colors = AppObjectsColors.cancelButtonColors()
                ) {
                    Text(stringResource(R.string.close))
                }
            }
        )
    }

    if (showOffsetEditor) {
        OffsetEditorDialog(
            initialOffset = editOffest,
            scope = scope,
            onDismiss = {
                showOffsetEditor = false
                editOffest = null
            }
        )
    }

    if (showReminderEditor) {
        val offsetToEdit = editOffest
        StyledReminderDialogs(
            initialOffset = offsetToEdit,
            onDismiss = { showReminderEditor = false }
        ) {
            scope.launch {
                if (offsetToEdit != null) ReminderSettingsStore.updateReminder(ctx, offsetToEdit, it)
                else ReminderSettingsStore.addReminder(ctx, it)
                showReminderEditor = false
            }
        }
    }

    if (showDeleteConfirm && editOffest != null) {
        val offsetToDelete = editOffest!!
        UserValidation(
            title = stringResource(R.string.delete_offset),
            message = "${stringResource(R.string.are_you_sure_to_delete_offset)}?",
            doNotRemindMeAgain = {
                scope.launch {
                    UserConfirmSettingsStore.set(ctx, UserConfirmEntry.SHOW_USER_VALIDATION_DELETE_OFFSET, false)
                }
            },
            onCancel = { showDeleteConfirm = false },
            onAgree = {
                showDeleteConfirm = false
                scope.launch {
                    ReminderSettingsStore.deleteReminder(ctx, offsetToDelete)
                }
            }
        )
    }
}