package org.elnix.notes.data

class ReminderRepository(private val dao: ReminderDao) {

    suspend fun getByNoteId(noteId: Long): List<ReminderEntity> =
        dao.getByNoteId(noteId)

    suspend fun insert(reminder: ReminderEntity): Long =
        dao.insert(reminder)

    suspend fun update(reminder: ReminderEntity) =
        dao.update(reminder)

    suspend fun delete(reminder: ReminderEntity) =
        dao.delete(reminder)
}
