package com.example.bloodpressureapp.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [User::class, Measurement::class, Therapy::class, Reminder::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): AppDao
}

