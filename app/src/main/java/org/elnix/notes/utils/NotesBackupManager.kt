// file: org/elnix/notes/utils/NotesBackupManager.kt
package org.elnix.notes.utils

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import org.elnix.notes.data.*
import java.io.InputStream
import java.io.OutputStream
import java.util.*

object NotesBackupManager {

    suspend fun exportNotes(ctx: Context, outputStream: OutputStream) {
        withContext(Dispatchers.IO) {
            val db = AppDatabase.get(ctx)
            val noteDao = db.noteDao()
            val reminderDao = db.reminderDao()

            // Get all notes (not Flow)
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
        }
    }

    suspend fun importNotes(ctx: Context, inputStream: InputStream) {
        withContext(Dispatchers.IO) {
            val jsonStr = inputStream.bufferedReader().readText()
            val obj = JSONObject(jsonStr)

            val db = AppDatabase.get(ctx)
            val noteDao = db.noteDao()
            val reminderDao = db.reminderDao()

            db.clearAllTables() // optional: clear before restore

            val notesArray = obj.getJSONArray("notes")
            val oldToNewNoteIds = mutableMapOf<Long, Long>()

            for (i in 0 until notesArray.length()) {
                val n = notesArray.getJSONObject(i)
                val oldId = n.getLong("id")
                val note = NoteEntity(
                    id = 0, // let Room assign new ID
                    title = n.getString("title"),
                    desc = n.getString("desc"),
                    createdAt = Date(n.getLong("createdAt")),
                    isCompleted = n.getBoolean("isCompleted")
                )
                val newId = noteDao.upsert(note)
                oldToNewNoteIds[oldId] = newId
            }

            val remindersArray = obj.optJSONArray("reminders") ?: JSONArray()
            for (i in 0 until remindersArray.length()) {
                val r = remindersArray.getJSONObject(i)
                val oldNoteId = r.getLong("noteId")
                val newNoteId = oldToNewNoteIds[oldNoteId] ?: continue

                val rem = ReminderEntity(
                    id = 0,
                    noteId = newNoteId,
                    dueDateTime = Calendar.getInstance().apply { timeInMillis = r.getLong("dueDateTime") },
                    enabled = r.getBoolean("enabled")
                )
                reminderDao.insert(rem)
            }
        }
    }
}
