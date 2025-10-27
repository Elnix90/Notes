package org.elnix.notes.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import org.elnix.notes.data.*
import java.io.InputStream
import java.io.OutputStream
import java.util.*

object NotesBackupManager {

    private const val TAG = "NotesBackupManager"

    suspend fun exportNotes(ctx: Context, outputStream: OutputStream) {
        withContext(Dispatchers.IO) {
            try {
                val db = AppDatabase.get(ctx)
                val noteDao = db.noteDao()
                val reminderDao = db.reminderDao()

                val notes = noteDao.getAll()
                val reminders = reminderDao.getAll()

                val json = JSONObject().apply {
                    put("notes", JSONArray().apply {
                        notes.forEach { note ->
                            put(JSONObject().apply {
                                put("id", note.id)
                                put("title", note.title)
                                put("desc", note.desc)
                                put("createdAt", note.createdAt.time)
                                put("isCompleted", note.isCompleted)
                            })
                        }
                    })
                    put("reminders", JSONArray().apply {
                        reminders.forEach { rem ->
                            put(JSONObject().apply {
                                put("id", rem.id)
                                put("noteId", rem.noteId)
                                put("dueDateTime", rem.dueDateTime.timeInMillis)
                                put("enabled", rem.enabled)
                            })
                        }
                    })
                }

                outputStream.bufferedWriter().use { it.write(json.toString(2)) }

                withContext(Dispatchers.Main) {
                    Toast.makeText(ctx, "Notes exported successfully", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Export successful, ${notes.size} notes and ${reminders.size} reminders exported.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error exporting notes", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(ctx, "Failed to export notes: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    suspend fun importNotes(ctx: Context, inputStream: InputStream) {
        withContext(Dispatchers.IO) {
            try {
                val jsonStr = inputStream.bufferedReader().readText()
                val obj = JSONObject(jsonStr)

                val db = AppDatabase.get(ctx)
                val noteDao = db.noteDao()
                val reminderDao = db.reminderDao()

                db.clearAllTables() // optional: clear before restore

                val notesArray = obj.optJSONArray("notes") ?: JSONArray()
                val oldToNewNoteIds = mutableMapOf<Long, Long>()

                for (i in 0 until notesArray.length()) {
                    val n = notesArray.getJSONObject(i)
                    val oldId = n.optLong("id", -1)
                    if (oldId == -1L) continue

                    val note = NoteEntity(
                        id = 0, // let Room assign new ID
                        title = n.optString("title", ""),
                        desc = n.optString("desc", ""),
                        createdAt = Date(n.optLong("createdAt", System.currentTimeMillis())),
                        isCompleted = n.optBoolean("isCompleted", false)
                    )
                    val newId = noteDao.upsert(note)
                    oldToNewNoteIds[oldId] = newId
                }

                val remindersArray = obj.optJSONArray("reminders") ?: JSONArray()
                for (i in 0 until remindersArray.length()) {
                    val r = remindersArray.getJSONObject(i)
                    val oldNoteId = r.optLong("noteId", -1)
                    val newNoteId = oldToNewNoteIds[oldNoteId] ?: continue

                    val rem = ReminderEntity(
                        id = 0,
                        noteId = newNoteId,
                        dueDateTime = Calendar.getInstance().apply { timeInMillis = r.optLong("dueDateTime", System.currentTimeMillis()) },
                        enabled = r.optBoolean("enabled", true)
                    )
                    reminderDao.insert(rem)
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(ctx, "Notes imported successfully", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Import successful: ${notesArray.length()} notes, ${remindersArray.length()} reminders.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error importing notes", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(ctx, "Failed to import notes: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
