package com.droid.itunesandroid.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.droid.itunesandroid.data.local.MovieDao
import com.droid.itunesandroid.model.Movie
import com.droid.itunesandroid.network.iTunesApi
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val movieDao: MovieDao,
    private val iTunesApi: iTunesApi,
    @ApplicationContext private val context: Context
) {
    private val sharedPreferences = context.getSharedPreferences("movie_prefs", Context.MODE_PRIVATE)

    // Keys for SharedPreferences
    companion object {
        private const val LAST_FETCH_TIME_KEY = "last_fetch_time"
        private const val FETCH_THRESHOLD = 24 * 60 * 60 * 1000 // 24 hours in milliseconds
        private const val LAST_SEARCH_TERM_KEY = "last_search_term"
    }

    // Clear only non-favorite movies from the database
    suspend fun clearNonFavoriteMovies() {
        movieDao.clearNonFavoriteMovies()
    }

    // Save the last search term
    fun saveLastSearchTerm(searchTerm: String) {
        sharedPreferences.edit().putString(LAST_SEARCH_TERM_KEY, searchTerm).apply()
    }

    // Retrieve the last search term
    fun getLastSearchTerm(): String? {
        return sharedPreferences.getString(LAST_SEARCH_TERM_KEY, "")
    }

    // Fetch fresh movies from the API and save them locally
    internal suspend fun fetchMoviesFromApi(searchTerm: String): List<Movie> {

        // Clear the previous search results
        movieDao.clearNonFavoriteMovies()

        // Fetch the new movies from the API
        val response = iTunesApi.searchMovies(searchTerm)
        val newMovies = response.results.map { movieDto ->
            Movie(
                trackId = movieDto.trackId,
                trackName = movieDto.trackName,
                artworkUrl = movieDto.artworkUrl100,
                price = movieDto.trackPrice,
                currency = movieDto.currency,
                genre = movieDto.primaryGenreName,
                shortDescription = movieDto.shortDescription ?: "-",
                longDescription = movieDto.longDescription ?: "No description available"
            )
        }

        // Fetch existing movies from the database to retain favorite status
        val existingMovies = movieDao.getAllMoviesList()  // Get the movies synchronously

        // Create a map of existing movies by trackId to check if they are favorites
        val favoriteMap = existingMovies.associateBy({ it.trackId }, { it.isFavorite })

        // Merge the new movies with the existing favorite status
        val mergedMovies = newMovies.map { movie ->
            movie.copy(isFavorite = favoriteMap[movie.trackId] ?: false)  // Retain favorite status if available
        }

        // Save the merged movies to the local database
        movieDao.insertMovies(mergedMovies)

        // Save the current time as the last fetched time
        sharedPreferences.edit().putLong(LAST_FETCH_TIME_KEY, System.currentTimeMillis()).apply()

        return mergedMovies
    }

    // Fetch movies from the local database (LiveData for real-time updates)
    fun getAllMovies(): LiveData<List<Movie>> {
        return movieDao.getAllMovies()  // Room will automatically update LiveData
    }

    // Fetch favorite movies from the local database (LiveData for real-time updates)
    fun getFavoriteMovies(): LiveData<List<Movie>> {
        return movieDao.getFavoriteMovies()
    }

    suspend fun updateFavorite(movie: Movie) {
        movieDao.update(movie)
    }

    // Fetch movie details by ID, fallback to API if not found locally
    suspend fun getMovieById(movieId: Long): Movie? {
        var movie = movieDao.getMovieById(movieId)
        if (movie == null) {
            // If not found locally, fetch from the API
            val response = iTunesApi.searchMovies(movieId.toString())
            if (response.results.isNotEmpty()) {
                val movieDto = response.results[0]
                movie = Movie(
                    trackId = movieDto.trackId,
                    trackName = movieDto.trackName,
                    artworkUrl = movieDto.artworkUrl100,
                    price = movieDto.trackPrice,
                    currency = movieDto.currency,
                    genre = movieDto.primaryGenreName,
                    shortDescription = movieDto.shortDescription ?: "-",
                    longDescription = movieDto.longDescription ?: "No description available"
                )
                // Optionally save in Room
                movieDao.insertMovies(listOf(movie))
            }
        }
        return movie
    }

    // Clear all movies from the local database
    suspend fun clearMovies() {
        movieDao.clearMovies()
    }
}

