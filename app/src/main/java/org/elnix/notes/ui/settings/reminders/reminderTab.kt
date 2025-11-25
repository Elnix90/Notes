package org.elnix.notes.ui.settings.reminders

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.elnix.notes.R
import org.elnix.notes.Routes
import org.elnix.notes.data.settings.stores.ReminderSettingsStore
import org.elnix.notes.ui.helpers.TextDivider
import org.elnix.notes.ui.helpers.reminders.OffsetPickerDialog
import org.elnix.notes.ui.helpers.reminders.TimeBubble
import org.elnix.notes.ui.helpers.settings.SettingsItem
import org.elnix.notes.ui.helpers.settings.SettingsLazyHeader
import org.elnix.notes.ui.security.AskNotificationButton
import org.elnix.notes.ui.theme.AppObjectsColors

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun RemindersTab(
    ctx: Context,
    activity: FragmentActivity,
    scope: CoroutineScope,
    navController: NavController,
    onBack: (() -> Unit)
) {
    val defaultReminders by ReminderSettingsStore.getDefaultRemindersFlow(ctx)
        .collectAsState(initial = emptyList())

    val allOffsets by ReminderSettingsStore.getReminders(ctx).collectAsState(initial = emptyList())


    var showOffsetPicker by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    var hasPermission by remember { mutableStateOf(false) }

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

    SettingsLazyHeader(
        title = stringResource(R.string.notification_reminders),
        onBack = onBack,
        helpText = stringResource(R.string.reminders_help_text),
        onReset = {
            scope.launch {
                ReminderSettingsStore.resetAll(ctx)
            }
        }
    ) {

        if (hasPermission){
            item { TextDivider(stringResource(R.string.default_reminders)) }

            item {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    defaultReminders.sortedBy { it.toCalendar().timeInMillis }
                        .forEach { reminder ->
                            TimeBubble(
                                reminderOffset = reminder,
                                onDelete = {
                                    val newList =
                                        defaultReminders.toMutableList().apply { remove(reminder) }
                                    scope.launch { ReminderSettingsStore.setDefaultReminders(ctx, newList) }
                                }
                            )
                        }

                    Button(
                        onClick = { showOffsetPicker = true },
                        colors = AppObjectsColors.buttonColors()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(15.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = stringResource(R.string.offsets)
                            )
                            Text(stringResource(R.string.customize_offsets))
                        }
                    }
                }
            }
        } else {
            item {
                Text(
                    text = stringResource(R.string.this_feature_needs_notification),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            item { AskNotificationButton(activity) }
        }

        item { TextDivider(stringResource(R.string.notifications)) }

        item {
            SettingsItem(
                title = stringResource(R.string.notifications),
                icon = Icons.Default.Notifications,
            ) { navController.navigate(Routes.Settings.RemindersSub.NOTIFICATIONS) }
        }
    }
    if (showOffsetPicker) {
        OffsetPickerDialog(
            offsets = allOffsets,
            activity = activity,
            onDismiss = { showOffsetPicker = false }
        ) { picked ->
            val newList = defaultReminders + picked
            scope.launch{
                ReminderSettingsStore.setDefaultReminders(ctx, newList)
                showOffsetPicker = false
            }
        }
    }
}

