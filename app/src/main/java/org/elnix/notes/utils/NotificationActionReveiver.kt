package org.elnix.notes.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.elnix.notes.data.AppDatabase
import org.elnix.notes.data.NoteRepository
import org.elnix.notes.data.ReminderEntity
import org.elnix.notes.data.ReminderRepository
import org.elnix.notes.data.settings.stores.NotificationActionType
import java.util.Calendar
import java.util.concurrent.TimeUnit

class NotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(ctx: Context, intent: Intent) {
        Log.d("Notification Intent", "Received intent: $intent")


        val actionTypeName = intent.getStringExtra("action_type") ?: return
        val reminderId = intent.getLongExtra("reminder_id", -1)
        val snoozeMinutes = intent.getIntExtra("snooze_minutes", 10)

        if (reminderId == -1L) return

        val actionType = NotificationActionType.valueOf(actionTypeName)

        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.get(ctx)
            val reminderRepo = ReminderRepository(db.reminderDao())
            val noteRepo = NoteRepository(db.noteDao())

            val reminder: ReminderEntity = reminderRepo.getById(reminderId) ?: return@launch
            val note = noteRepo.getById(reminder.noteId) ?: return@launch

            Log.d("NotificationAction", "Received action: $actionTypeName")

            when (actionType) {
                NotificationActionType.MARK_COMPLETED -> {
                    noteRepo.upsert(note.copy(isCompleted = true))
                    showToast(ctx, "Note marked as completed!")
                }

                NotificationActionType.SNOOZE -> {
                    val newTime = Calendar.getInstance().apply { add(Calendar.MINUTE, snoozeMinutes) }
                    val updatedReminder = reminder.copy(dueDateTime = newTime)
                    reminderRepo.update(updatedReminder)

                    // Reschedule notification
                    val data = workDataOf(
                        "title" to note.title,
                        "reminder_id" to reminder.id
                    )
                    val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                        .setInputData(data)
                        .setInitialDelay(snoozeMinutes.toLong(), TimeUnit.MINUTES)
                        .build()
                    WorkManager.getInstance(ctx).enqueue(workRequest)

                    showToast(ctx, "Reminder snoozed for $snoozeMinutes minutes")
                }

                NotificationActionType.DELETE -> {
                    noteRepo.delete(note)
                    showToast(ctx, "Note deleted")
                }
            }
        }
    }

    private fun showToast(ctx: Context, message: String) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show()
        }
    }
}
