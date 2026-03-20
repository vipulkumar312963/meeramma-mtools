package org.meerammafoundation.tools.budget.reminder

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [BillReminder::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class BillReminderDatabase : RoomDatabase() {

    abstract fun billReminderDao(): BillReminderDao

    companion object {
        private const val DB_NAME = "bill_reminder_database"

        @Volatile
        private var INSTANCE: BillReminderDatabase? = null

        fun getDatabase(context: Context): BillReminderDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BillReminderDatabase::class.java,
                    DB_NAME
                )
                    .fallbackToDestructiveMigration()
                    .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}