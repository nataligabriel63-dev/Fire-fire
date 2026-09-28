package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [SavedProfile::class],
    version = 1,
    exportSchema = false
)
abstract class CeifadorDatabase : RoomDatabase() {
    abstract fun ceifadorDao(): CeifadorDao

    companion object {
        @Volatile
        private var INSTANCE: CeifadorDatabase? = null

        fun getInstance(context: Context): CeifadorDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CeifadorDatabase::class.java,
                    "ceifador_sensi.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
