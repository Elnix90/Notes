package org.elnix.notes.ui

import android.annotation.SuppressLint
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
import org.elnix.notes.data.helpers.NoteType
import org.elnix.notes.data.settings.stores.OffsetsSettingsStore
import org.elnix.notes.data.settings.stores.ReminderSettingsStore
import org.elnix.notes.utils.ReminderOffset
import kotlin.random.Random

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    private val ctx = application.applicationContext
    private val noteRepo = NoteRepository(AppDatabase.get(ctx).noteDao())
    private val reminderRepo = ReminderRepository(AppDatabase.get(ctx).reminderDao())

    val notes = noteRepo.observeAll()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())



    // --- Notes ---
    suspend fun addNoteAndReturnId(title: String = "", desc: String = "", type: NoteType): Long {
        val currentNotes = noteRepo.observeAll().firstOrNull() ?: emptyList()
        val note = NoteEntity(title = title, desc = desc, type = type, orderIndex = currentNotes.size)
        val id = noteRepo.upsert(note)

        val defaultsReminders = ReminderSettingsStore.getDefaultRemindersFlow(ctx).firstOrNull() ?: emptyList()
        val defaultsOffsets = OffsetsSettingsStore.getDefaultOffsetsFlow(ctx).firstOrNull() ?: emptyList()

        defaultsReminders.forEach { offset ->
            val cal = offset.toCalendar()
            reminderRepo.insert(ReminderEntity(noteId = id, dueDateTime = cal, enabled = true))
        }

        defaultsOffsets.forEach { offset ->
            val cal = ReminderOffset(secondsFromNow = offset.offset.toLong()).toCalendar()
            reminderRepo.insert(ReminderEntity(noteId = id, dueDateTime = cal, enabled = true))
        }

        return id
    }

    fun reorderNotes(newList: List<NoteEntity>) {
        viewModelScope.launch {
            newList.forEachIndexed { index, note ->
                if (note.orderIndex != index) {
                    noteRepo.upsert(note.copy(orderIndex = index))
                }
            }
        }
    }

    suspend fun duplicateNote(noteId: Long): Long {
        val note = noteRepo.getById(noteId) ?: return -1L
        val currentNotes = noteRepo.observeAll().firstOrNull() ?: emptyList()

        val duplicatedNote = note.copy(
            id = 0, // ensure new insert
            title = note.title + " (copy)",
            orderIndex = currentNotes.size
        )

        val newId = noteRepo.upsert(duplicatedNote)

        reminderRepo.duplicateReminders(noteId, newId)

        return newId
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
            .filter { it.title.isBlank() && it.desc.isBlank() && it.checklist.isEmpty() }
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


    // --- DEBUG FEATURES ---
    suspend fun createFakeNotes(number: Int): List<Long> {
        val ids = mutableListOf<Long>()
        repeat(number) {
            val id = Random.nextLong().toString()
            val title = "Fake Note $id"
            val description = "This is a description for fake note $id."
            val possibleTypes = listOf(NoteType.CHECKLIST, NoteType.TEXT)
            val noteId = addNoteAndReturnId(title, description, type = possibleTypes.random())
            ids += noteId
        }
        return ids
    }



    // Complete / Un-complete notes
    fun markCompleted(note: NoteEntity) = viewModelScope.launch {
        noteRepo.upsert(note.copy(isCompleted = true))
    }

    fun markUnCompleted(note: NoteEntity) = viewModelScope.launch {
        noteRepo.upsert(note.copy(isCompleted = false))
    }

}
