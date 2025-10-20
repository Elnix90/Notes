package org.elnix.notes.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val ctx = application.applicationContext
    private val noteRepo = NoteRepository(AppDatabase.get(ctx).noteDao())
    private val reminderRepo = ReminderRepository(AppDatabase.get(ctx).reminderDao())

    val notes = noteRepo.observeAll()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // --- Notes ---
    fun addNote(title: String, desc: String) = viewModelScope.launch {
        val note = NoteEntity(title = title, desc = desc, createdAt = Date())
        val noteId = noteRepo.upsert(note)

        // Apply default reminders
        val defaults = SettingsStore.getDefaultRemindersFlow(ctx).firstOrNull() ?: emptyList()
        defaults.forEach { offset ->
            val cal = offset.toCalendar()
            reminderRepo.insert(ReminderEntity(noteId = noteId, dueDateTime = cal, enabled = true))
        }
    }

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

    fun addReminder(reminder: ReminderEntity) = viewModelScope.launch { reminderRepo.insert(reminder) }
    fun updateReminder(reminder: ReminderEntity) = viewModelScope.launch { reminderRepo.update(reminder) }
    fun deleteReminder(reminder: ReminderEntity) = viewModelScope.launch { reminderRepo.delete(reminder) }
}
