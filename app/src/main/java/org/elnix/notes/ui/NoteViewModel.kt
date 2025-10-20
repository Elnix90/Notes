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
import java.util.Calendar
import java.util.Date

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = NoteRepository(AppDatabase.get(application).noteDao())

    val notes = repo.observeAll()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Updated addNote to include dueDateTime and reminderEnabled
    fun addNote(
        title: String,
        desc: String,
        dueDateTime: Calendar? = null,
        reminderEnabled: Boolean = false
    ) = viewModelScope.launch {
        val note = NoteEntity(
            title = title,
            desc = desc,
            createdAt = Date(),
            dueDateTime = dueDateTime,
            reminderEnabled = reminderEnabled
        )
        repo.upsert(note)
    }

    fun update(note: NoteEntity) = viewModelScope.launch { repo.upsert(note) }

    fun delete(note: NoteEntity) = viewModelScope.launch { repo.delete(note) }

    suspend fun getById(id: Long): NoteEntity? = repo.getById(id)
}
