package org.elnix.notes.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.elnix.notes.R
import kotlin.random.Random

class NotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val title = inputData.getString("title") ?: return Result.failure()

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            "tasks_channel",
            "Tasks",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        val taskReminderText = applicationContext.getString(R.string.task_reminder)

        val notification = NotificationCompat.Builder(applicationContext, "tasks_channel")
            .setContentTitle(taskReminderText)
            .setContentText(title)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        notificationManager.notify(Random.nextInt(), notification)
        return Result.success()
    }
}
