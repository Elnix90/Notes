package org.elnix.notes.data

import kotlinx.coroutines.flow.Flow

class ReminderRepository(private val dao: ReminderDao) {

    fun observeByNoteId(noteId: Long): Flow<List<ReminderEntity>> =
        dao.observeByNoteId(noteId)

    suspend fun insert(reminder: ReminderEntity): Long =
        dao.insert(reminder)

    suspend fun update(reminder: ReminderEntity) =
        dao.update(reminder)

    suspend fun delete(reminder: ReminderEntity) =
        dao.delete(reminder)
}
