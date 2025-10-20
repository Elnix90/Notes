package org.elnix.notes.utils

import android.content.Context

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.Calendar
import java.util.concurrent.TimeUnit

fun scheduleNotification(context: Context, title: String, dueDateTime: Calendar) {
    val now = Calendar.getInstance()
    val delay = dueDateTime.timeInMillis - now.timeInMillis
    if (delay <= 0) return // past date, no need to schedule

    val data = workDataOf("title" to title)

    val request = OneTimeWorkRequestBuilder<NotificationWorker>()
        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
        .setInputData(data)
        .build()

    WorkManager.getInstance(context).enqueue(request)
}
