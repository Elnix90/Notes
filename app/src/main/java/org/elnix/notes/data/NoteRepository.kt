// file: org/elnix/notes/data/NoteRepository.kt
package org.elnix.notes.data

import kotlinx.coroutines.flow.Flow

class NoteRepository(private val dao: NoteDao) {
    fun observeAll(): Flow<List<NoteEntity>> = dao.observeAll()

    suspend fun getById(id: Long): NoteEntity? = dao.getById(id)

    suspend fun upsert(note: NoteEntity): Long {
        val noteEdited = note.copy(lastEdit = System.currentTimeMillis())
        return if (noteEdited.id == 0L) dao.upsert(noteEdited) else { dao.update(noteEdited); noteEdited.id }
    }

    suspend fun delete(note: NoteEntity) = dao.delete(note)
}
