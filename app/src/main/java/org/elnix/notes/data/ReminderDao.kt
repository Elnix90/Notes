package org.elnix.notes.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders WHERE noteId = :noteId")
    fun observeByNoteId(noteId: Long): Flow<List<ReminderEntity>>

    @Insert
    suspend fun insert(reminder: ReminderEntity): Long

    @Update
    suspend fun update(reminder: ReminderEntity)

    @Delete
    suspend fun delete(reminder: ReminderEntity)
}
