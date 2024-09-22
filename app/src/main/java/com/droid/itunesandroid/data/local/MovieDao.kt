package com.droid.itunesandroid.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.droid.itunesandroid.model.Movie

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<Movie>)

    @Query("DELETE FROM movies")
    suspend fun clearMovies()

    @Query("DELETE FROM movies WHERE isFavorite = 0")
    suspend fun clearNonFavoriteMovies()  // Clear only non-favorite movies

    @Query("SELECT * FROM movies")
    fun getAllMovies(): LiveData<List<Movie>>  // LiveData for observing movies

    @Query("SELECT * FROM movies WHERE isFavorite = 1")
    fun getFavoriteMovies(): LiveData<List<Movie>>  // LiveData for observing favorite movies

    @Query("SELECT * FROM movies")
    suspend fun getAllMoviesList(): List<Movie>  // Synchronous fetch for merging favorites

    // Fetch all favorite movies synchronously (for merging with search results)
    @Query("SELECT * FROM movies WHERE isFavorite = 1")
    suspend fun getFavoriteMoviesSync(): List<Movie>

    @Update
    suspend fun update(movie: Movie)

    @Query("SELECT * FROM movies WHERE trackId = :movieId")
    suspend fun getMovieById(movieId: Long): Movie?
}

