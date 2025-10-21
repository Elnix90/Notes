package org.elnix.notes.utils

import android.content.Context
import androidx.work.ExistingWorkPolicy

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import org.elnix.notes.data.ReminderEntity
import java.util.Calendar
import java.util.concurrent.TimeUnit

fun scheduleReminderNotification(context: Context, reminder: ReminderEntity, title: String) {
    val now = Calendar.getInstance()
    val delay = reminder.dueDateTime.timeInMillis - now.timeInMillis
    if (delay <= 0) return

    val data = workDataOf("title" to title)

    val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
        .setInputData(data)
        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
        .build()

    WorkManager.getInstance(context).enqueueUniqueWork(
        reminder.id.toString(),  // unique work name per reminder ID
        ExistingWorkPolicy.REPLACE,
        workRequest
    )
}

fun cancelReminderNotification(context: Context, reminderId: Long) {
    WorkManager.getInstance(context).cancelUniqueWork(reminderId.toString())
}
