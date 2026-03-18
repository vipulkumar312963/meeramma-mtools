package org.meerammafoundation.tools.billSplitter

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Group::class, Member::class, Bill::class, BillShare::class],
    version = 1,
    exportSchema = false
)
abstract class BillSplitterDatabase : RoomDatabase() {
    abstract fun groupDao(): GroupDao
    abstract fun memberDao(): MemberDao
    abstract fun billDao(): BillDao
    abstract fun billShareDao(): BillShareDao

    companion object {
        @Volatile
        private var INSTANCE: BillSplitterDatabase? = null

        fun getDatabase(context: Context): BillSplitterDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BillSplitterDatabase::class.java,
                    "bill_splitter_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}