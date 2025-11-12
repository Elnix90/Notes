package org.elnix.notes.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.elnix.notes.data.AppDatabase
import org.elnix.notes.data.ChecklistItem
import org.elnix.notes.data.NoteEntity
import org.elnix.notes.data.ReminderEntity
import org.elnix.notes.data.helpers.NoteType
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.io.OutputStream
import java.util.Calendar
import java.util.Date

object NotesBackupManager {

    private const val TAG = "NotesBackupManager"

    // ---------- EXPORT ----------
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
                                put("checklist", JSONArray().apply {
                                    note.checklist.forEach { item ->
                                        put(JSONObject().apply {
                                            put("text", item.text)
                                            put("checked", item.checked)
                                        })
                                    }
                                })
                                put("tagIds", JSONArray(note.tagIds))
                                put("bgColor", note.bgColor?.value ?: JSONObject.NULL)
                                put("txtColor", note.txtColor?.value ?: JSONObject.NULL)
                                put("autoTextColor", note.autoTextColor)
                                put("isCompleted", note.isCompleted)
                                put("type", note.type.name)
                                put("dueDateTime", note.dueDateTime?.timeInMillis ?: JSONObject.NULL)
                                put("createdAt", note.createdAt.time)
                                put("lastEdit", note.lastEdit)
                                put("orderIndex", note.orderIndex)
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
                    Log.d(TAG, "Export successful: ${notes.size} notes, ${reminders.size} reminders.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error exporting notes", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(ctx, "Export failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // ---------- IMPORT ----------
    suspend fun importNotes(ctx: Context, inputStream: InputStream) {
        withContext(Dispatchers.IO) {
            try {
                val jsonStr = inputStream.bufferedReader().readText()
                val obj = JSONObject(jsonStr)

                val db = AppDatabase.get(ctx)
                val noteDao = db.noteDao()
                val reminderDao = db.reminderDao()

                db.clearAllTables()

                val notesArray = obj.optJSONArray("notes") ?: JSONArray()
                val oldToNewNoteIds = mutableMapOf<Long, Long>()

                for (i in 0 until notesArray.length()) {
                    val n = notesArray.getJSONObject(i)
                    val oldId = n.optLong("id", -1)
                    if (oldId == -1L) continue

                    val checklist = n.optJSONArray("checklist")?.let { arr ->
                        (0 until arr.length()).map { j ->
                            val item = arr.getJSONObject(j)
                            ChecklistItem(
                                text = item.optString("text", ""),
                                checked = item.optBoolean("checked", false)
                            )
                        }
                    } ?: emptyList()

                    val tagIds = n.optJSONArray("tagIds")?.let { arr ->
                        (0 until arr.length()).map { arr.optLong(it) }
                    } ?: emptyList()

                    val bgColor = n.optLong("bgColor", Long.MIN_VALUE)
                        .takeIf { it != Long.MIN_VALUE }?.let { Color(it) }

                    val txtColor = n.optLong("txtColor", Long.MIN_VALUE)
                        .takeIf { it != Long.MIN_VALUE }?.let { Color(it) }

                    val note = NoteEntity(
                        title = n.optString("title", ""),
                        desc = n.optString("desc", ""),
                        checklist = checklist,
                        tagIds = tagIds,
                        bgColor = bgColor,
                        txtColor = txtColor,
                        autoTextColor = n.optBoolean("autoTextColor", true),
                        isCompleted = n.optBoolean("isCompleted", false),
                        type = NoteType.valueOf(n.optString("type", NoteType.TEXT.name)),
                        dueDateTime = n.optLong("dueDateTime", -1).takeIf { it > 0 }?.let {
                            Calendar.getInstance().apply { timeInMillis = it }
                        },
                        createdAt = Date(n.optLong("createdAt", System.currentTimeMillis())),
                        lastEdit = n.optLong("lastEdit", System.currentTimeMillis()),
                        orderIndex = n.optInt("orderIndex", 0)
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
                        noteId = newNoteId,
                        dueDateTime = Calendar.getInstance().apply {
                            timeInMillis = r.optLong("dueDateTime", System.currentTimeMillis())
                        },
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
                    Toast.makeText(ctx, "Import failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
