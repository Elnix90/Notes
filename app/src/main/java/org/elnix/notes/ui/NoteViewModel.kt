package org.elnix.notes.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.elnix.notes.data.AppDatabase
import org.elnix.notes.data.NoteEntity
import org.elnix.notes.data.NoteRepository
import org.elnix.notes.data.ReminderEntity
import org.elnix.notes.data.ReminderRepository
import java.util.Date

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val noteRepo = NoteRepository(AppDatabase.get(application).noteDao())
    private val reminderRepo = ReminderRepository(AppDatabase.get(application).reminderDao())

    val notes = noteRepo.observeAll()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // --- Notes ---
    fun addNote(title: String, desc: String) = viewModelScope.launch {
        val note = NoteEntity(title = title, desc = desc, createdAt = Date())
        noteRepo.upsert(note)
    }

    fun update(note: NoteEntity) = viewModelScope.launch { noteRepo.upsert(note) }
    fun delete(note: NoteEntity) = viewModelScope.launch { noteRepo.delete(note) }
    suspend fun getById(id: Long): NoteEntity? = noteRepo.getById(id)

    // --- Reminders ---
    suspend fun getReminders(noteId: Long): List<ReminderEntity> =
        reminderRepo.getByNoteId(noteId)

    fun addReminder(reminder: ReminderEntity) = viewModelScope.launch {
        reminderRepo.insert(reminder)
    }

    fun updateReminder(reminder: ReminderEntity) = viewModelScope.launch {
        reminderRepo.update(reminder)
    }

    fun deleteReminder(reminder: ReminderEntity) = viewModelScope.launch {
        reminderRepo.delete(reminder)
    }
}

