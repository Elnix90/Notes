// file: org/elnix/notes/data/AppDatabase.kt
package org.elnix.notes.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.elnix.notes.data.converters.ColorConverter
import org.elnix.notes.data.converters.Converters

@Database(entities = [NoteEntity::class, ReminderEntity::class], version = 1)
@TypeConverters(Converters::class,ColorConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun reminderDao(): ReminderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "notes_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }

        fun reset(context: Context) {
            // Close the current instance (if any)
            INSTANCE?.close()
            INSTANCE = null

            // Delete the physical database file
            context.deleteDatabase("notes_db")

            // Recreate a fresh instance
            get(context)
        }

    }
}
