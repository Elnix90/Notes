package org.elnix.notes.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.elnix.notes.data.AppDatabase
import org.elnix.notes.data.NoteEntity
import org.elnix.notes.data.NoteRepository
import org.elnix.notes.data.ReminderEntity
import org.elnix.notes.data.ReminderRepository
import org.elnix.notes.data.SettingsStore
import java.util.Date
import kotlin.random.Random

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val ctx = application.applicationContext
    private val noteRepo = NoteRepository(AppDatabase.get(ctx).noteDao())
    private val reminderRepo = ReminderRepository(AppDatabase.get(ctx).reminderDao())

    val notes = noteRepo.observeAll()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())



    // --- Notes ---
    suspend fun addNoteAndReturnId(title: String, desc: String): Long {
        val note = NoteEntity(title = title, desc = desc, createdAt = Date())
        val id = noteRepo.upsert(note)

        val defaults = SettingsStore.getDefaultRemindersFlow(ctx).firstOrNull() ?: emptyList()
        defaults.forEach { offset ->
            val cal = offset.toCalendar()
            reminderRepo.insert(ReminderEntity(noteId = id, dueDateTime = cal, enabled = true))
        }
        return id
    }

    fun update(note: NoteEntity) = viewModelScope.launch { noteRepo.upsert(note) }
    fun delete(note: NoteEntity) = viewModelScope.launch { noteRepo.delete(note) }
    suspend fun getById(id: Long): NoteEntity? = noteRepo.getById(id)



    // --- Reminders ---
    fun remindersFor(noteId: Long): StateFlow<List<ReminderEntity>> =
        reminderRepo.observeByNoteId(noteId)
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    suspend fun addReminder(reminder: ReminderEntity): Long {
        return reminderRepo.insert(reminder)
    }

    fun updateReminder(reminder: ReminderEntity) = viewModelScope.launch { reminderRepo.update(reminder) }
    fun deleteReminder(reminder: ReminderEntity) = viewModelScope.launch { reminderRepo.delete(reminder) }



    //  Deletes all notes that have both a blank title and description.
    suspend fun deleteAllEmptyNotes() {
        val allNotes = noteRepo.observeAll().first() // get current list
        allNotes
            .filter { it.title.isBlank() && it.desc.isBlank() }
            .forEach { noteRepo.delete(it) }
    }



    // DEBUG FEATURES
    fun deleteAllReminders() = viewModelScope.launch {
        val allNotes = noteRepo.observeAll().first()
        allNotes.forEach { note ->
            reminderRepo.deleteByNoteId(note.id) // clears reminders from DB
        }
    }

    fun disableAllReminders() = viewModelScope.launch {
        val allNotes = noteRepo.observeAll().first()
        allNotes.forEach { note ->
            reminderRepo.observeByNoteId(note.id).first().forEach { reminder ->
                reminderRepo.update(reminder.copy(enabled = false))
            }
        }
    }

    fun enableAllReminders() = viewModelScope.launch {
        val allNotes = noteRepo.observeAll().first()
        allNotes.forEach { note ->
            reminderRepo.observeByNoteId(note.id).first().forEach { reminder ->
                reminderRepo.update(reminder.copy(enabled = true))
            }
        }
    }

    fun cancelAllPendingNotifications(context: Context) {
        WorkManager.getInstance(context).cancelAllWork()
    }

    fun createFakeNotes() {
        viewModelScope.launch {
            repeat(10) { it ->
                val id = Random.nextLong().toString()
                val title = "Fake Note $id"
                val description = "This is a description for fake note $id."
                addNoteAndReturnId(title, description)
            }
        }
    }






    // Complete / Un-complete notes
    fun markCompleted(note: NoteEntity) = viewModelScope.launch {
        noteRepo.upsert(note.copy(isCompleted = true))
    }

    fun markUnCompleted(note: NoteEntity) = viewModelScope.launch {
        noteRepo.upsert(note.copy(isCompleted = false))
    }

}
