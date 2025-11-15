package org.elnix.notes.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.first
import org.elnix.notes.MainActivity
import org.elnix.notes.R
import org.elnix.notes.data.settings.stores.NotificationActionType
import org.elnix.notes.data.settings.stores.NotificationsSettingsStore

class NotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val title = inputData.getString("title") ?: return Result.failure()
        val noteId = inputData.getLong("note_id", -1L)
        val noteType = inputData.getString("note_type") ?: return Result.failure()
        val reminderId = inputData.getLong("reminder_id", -1L)

        if (noteId == -1L || reminderId == -1L) return  Result.failure()

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create channel
        val channel = NotificationChannel(
            "tasks_channel",
            "Tasks",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        val taskReminderText = applicationContext.getString(R.string.task_reminder)

        val builder = NotificationCompat.Builder(applicationContext, "tasks_channel")
            .setContentTitle(taskReminderText)
            .setContentText(title)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)

        // Load enabled notification actions from the store
        val actions = NotificationsSettingsStore.getSettingsFlow(applicationContext).first()
            .filter { it.enabled }

        actions.forEach { setting ->
            val intent = Intent(applicationContext, NotificationActionReceiver::class.java).apply {
                putExtra("action_type", setting.actionType.name)
                putExtra("reminder_id", reminderId)
                if (setting.actionType == NotificationActionType.SNOOZE) {
                    putExtra("snooze_minutes", setting.snoozeMinutes)
                }
            }

            Log.d("NotificationWorker", "created $intent")

            val pendingIntent = PendingIntent.getBroadcast(
                applicationContext,
                setting.actionType.ordinal + reminderId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            builder.addAction(
                android.R.drawable.ic_menu_send,
                setting.actionType.name.replace("_", " "),
                pendingIntent
            )
        }

        val openIntent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            // This will be read by MainActivity to navigate
            putExtra("open_note_id", noteId)
            putExtra("open_note_type", noteType)
        }

        val openPendingIntent = PendingIntent.getActivity(
            applicationContext,
            reminderId.toInt(),
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        builder.setContentIntent(openPendingIntent)


        // Show the notification
        notificationManager.notify(reminderId.toInt(), builder.build())

        return Result.success()
    }
}

