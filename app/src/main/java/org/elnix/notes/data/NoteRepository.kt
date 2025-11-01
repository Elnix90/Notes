// file: org/elnix/notes/data/NoteRepository.kt
package org.elnix.notes.data

import kotlinx.coroutines.flow.Flow

class NoteRepository(private val dao: NoteDao) {
    fun observeAll(): Flow<List<NoteEntity>> = dao.observeAll()

    suspend fun getById(id: Long): NoteEntity? = dao.getById(id)

    suspend fun upsert(note: NoteEntity): Long {
        // if id == 0 insert, else update (insert with REPLACE works too)
        return if (note.id == 0L) dao.upsert(note) else { dao.update(note); note.id }
    }

    suspend fun delete(note: NoteEntity) = dao.delete(note)

//    suspend fun deleteById(id: Long) = dao.deleteById(id)
}
