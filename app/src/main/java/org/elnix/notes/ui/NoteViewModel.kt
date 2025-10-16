// file: org/elnix/notes/ui/NoteViewModel.kt
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
import java.util.Date

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = NoteRepository(AppDatabase.get(application).noteDao())

    val notes = repo.observeAll()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun addNote(title: String, desc: String) = viewModelScope.launch {
        val n = NoteEntity(
            title = title,
            desc = desc,
            createdAt = Date()
        )
        repo.upsert(n)
    }

    fun update(note: NoteEntity) = viewModelScope.launch { repo.upsert(note) }

    fun delete(note: NoteEntity) = viewModelScope.launch { repo.delete(note) }

    suspend fun getById(id: Long): NoteEntity? = repo.getById(id)

}
