package org.elnix.notes.ui.settings.reminders

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.elnix.notes.R
import org.elnix.notes.data.ReminderEntity
import org.elnix.notes.data.settings.stores.OffsetsSettingsStore
import org.elnix.notes.data.settings.stores.ReminderSettingsStore
import org.elnix.notes.data.settings.stores.ReminderSettingsStore.getDefaultRemindersFlow
import org.elnix.notes.data.settings.stores.ReminderSettingsStore.setDefaultReminders
import org.elnix.notes.ui.helpers.TextDivider
import org.elnix.notes.ui.helpers.reminders.OffsetPickerDialog
import org.elnix.notes.ui.helpers.reminders.ReminderPicker
import org.elnix.notes.ui.helpers.reminders.TimeBubble
import org.elnix.notes.ui.settings.SettingsLazyHeader
import org.elnix.notes.ui.theme.AppObjectsColors

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun RemindersTab(
    ctx: Context,
    activity: FragmentActivity,
    scope: CoroutineScope,
    onBack: (() -> Unit)
) {
    val defaultReminders by getDefaultRemindersFlow(ctx)
        .collectAsState(initial = emptyList())

    val allOffsets by OffsetsSettingsStore.getOffsets(ctx).collectAsState(initial = emptyList())
    var showOffsetPicker by remember { mutableStateOf(false) }

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

        item { TextDivider(stringResource(R.string.default_reminders)) }

        item {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                defaultReminders.sortedBy { it.toCalendar().timeInMillis }
                    .forEachIndexed { index, reminder ->
                        TimeBubble(
                            reminder = ReminderEntity(
                                noteId = -1,
                                dueDateTime = reminder.toCalendar(),
                                enabled = true
                            ),
                            onDelete = {
                                val newList =
                                    defaultReminders.toMutableList().apply { removeAt(index) }
                                scope.launch { setDefaultReminders(ctx, newList) }
                            }
                        )
                    }
            }
        }
        item {
            ReminderPicker(activity) { picked ->
                val newList = defaultReminders + picked
                scope.launch { setDefaultReminders(ctx, newList) }
            }
        }


        item { TextDivider(stringResource(R.string.offsets)) }

        item {
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
    if (showOffsetPicker) {
        OffsetPickerDialog(
            offsets = allOffsets,
            activity = activity,
            showDatePicker = false,
            onDismiss = { showOffsetPicker = false }
        ) { showOffsetPicker = false }
    }
}

