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

    suspend fun deleteByNoteId(noteId: Long) = dao.deleteByNoteId(noteId)

    suspend fun duplicateReminders(fromNoteId: Long, toNoteId: Long) {
        val oldReminders = dao.getByNoteId(fromNoteId)
        oldReminders.forEach { reminder ->
            dao.insert(
                reminder.copy(
                    id = 0, // force new ID
                    noteId = toNoteId
                )
            )
        }
    }
}
