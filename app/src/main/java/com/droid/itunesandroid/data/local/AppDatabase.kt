package com.droid.itunesandroid.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.droid.itunesandroid.model.Movie

@Database(entities = [Movie::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}