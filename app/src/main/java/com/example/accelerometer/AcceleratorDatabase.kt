package com.example.accelerometer
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Accelerator::class], version = 2)

abstract class AcceleratorDatabase : RoomDatabase(){
    abstract fun acceleratorDao(): AcceleratorDao
    object DatabaseFactory {
        private var INSTANCE: AcceleratorDatabase? = null

        fun getInstance(context: Context): AcceleratorDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AcceleratorDatabase::class.java,
                    "accelerator_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}