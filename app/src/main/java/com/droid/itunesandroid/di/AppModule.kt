package com.droid.itunesandroid.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.droid.itunesandroid.data.local.AppDatabase
import com.droid.itunesandroid.data.local.MovieDao
import com.droid.itunesandroid.util.SharedPreferencesHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Migration from version 1 to version 2
    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Adding the new column
            db.execSQL("ALTER TABLE movies ADD COLUMN currency TEXT NOT NULL DEFAULT ''")
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "movie_database"
        )
            .addMigrations(MIGRATION_2_3)
            .build()
    }

    @Provides
    @Singleton
    fun provideMovieDao(database: AppDatabase): MovieDao {
        return database.movieDao()
    }

    @Provides
    @Singleton
    fun provideSharedPreferencesHelper(
        @ApplicationContext context: Context
    ): SharedPreferencesHelper {
        return SharedPreferencesHelper(context)
    }
}
