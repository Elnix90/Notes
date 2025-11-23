package org.elnix.notes.ui

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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
import org.elnix.notes.data.helpers.SortMode
import org.elnix.notes.data.helpers.SortType
import org.elnix.notes.data.settings.stores.LockSettingsStore
import org.elnix.notes.data.settings.stores.ReminderSettingsStore
import org.elnix.notes.data.settings.stores.SortSettingsStore
import kotlin.random.Random

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    private val ctx = application.applicationContext
    private val noteRepo = NoteRepository(AppDatabase.get(ctx).noteDao())
    private val reminderRepo = ReminderRepository(AppDatabase.get(ctx).reminderDao())

    val sortModeFlow = SortSettingsStore.getSortMode(ctx)
    val sortTypeFlow = SortSettingsStore.getSortType(ctx)

    val notes = combine(
        noteRepo.observeAll(),
        sortTypeFlow,
        sortModeFlow
    ) { notes, type, mode ->

        val sorted = when (type) {

            SortType.CUSTOM -> notes.sortedBy { it.orderIndex }

            SortType.DATE -> notes.sortedBy { it.lastEdit }

            SortType.TITLE -> notes.sortedBy { it.title.lowercase() }

            SortType.COMPLETED -> notes.sortedBy { note ->
                !note.isCompleted
            }
        }

        if (mode == SortMode.ASC) sorted else sorted.reversed()
    }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())



    // --- Notes ---
    suspend fun addNoteAndReturnId(title: String = "", desc: String = "", type: NoteType): Long {
        val currentNotes = noteRepo.observeAll().firstOrNull() ?: emptyList()
        val note = NoteEntity(title = title, desc = desc, type = type, orderIndex = currentNotes.size)
        val id = noteRepo.upsert(note)

        val defaultsReminders = ReminderSettingsStore.getDefaultRemindersFlow(ctx).firstOrNull() ?: emptyList()

        defaultsReminders.forEach { offset ->
            val cal = offset.toCalendar()
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


    // Lock Screen
    private val _locked = MutableStateFlow(true)
    private val _ignoreBackgroundLock = MutableStateFlow(false)

    val locked = _locked.asStateFlow()

    fun onAppBackground() {
        Log.d("LifeCycle","App went background")
        if (!_ignoreBackgroundLock.value) {
            _locked.value = true
        }
    }

    fun onAppForeground() {
        viewModelScope.launch {
            Log.d("LifeCycle","App went foreground")

            val settings = LockSettingsStore.getLockSettings(ctx).first()
            val lastUnlock = settings.lastUnlockTimestamp
            val timeout = settings.lockTimeoutSeconds

            // If timeout = 0 → lock if not in file picker mode (ignore background lock)
            if (timeout == 0) {
                _locked.value = !_ignoreBackgroundLock.value
                return@launch
            }

            val now = System.currentTimeMillis()
            val elapsed = (now - lastUnlock) / 1000

            _locked.value = if (!_ignoreBackgroundLock.value) elapsed >= timeout else false
            _ignoreBackgroundLock.value = false
        }
    }


    fun unlock() { _locked.value = false }

    fun enableIgnoreBackgroundLock() { _ignoreBackgroundLock.value = true }
}
