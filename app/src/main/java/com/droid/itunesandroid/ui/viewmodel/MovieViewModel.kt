package com.droid.itunesandroid.ui.viewmodel

import android.util.Log
import com.droid.itunesandroid.data.repository.MovieRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droid.itunesandroid.model.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _selectedMovie = MutableLiveData<Movie>()
    val selectedMovie: LiveData<Movie> get() = _selectedMovie

    val favoriteMovies: LiveData<List<Movie>> = repository.getFavoriteMovies()

    // LiveData for all movies (Room will automatically update this when favorites are changed)
    val movies: LiveData<List<Movie>> = repository.getAllMovies()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // Retain the last search term to avoid fetching again unnecessarily
    private var lastSearchTerm: String? = null

    // Retrieve the last search term from SharedPreferences
    fun getLastSearchTerm(): String? {
        return repository.getLastSearchTerm()
    }

    // Search movies from the API and update the UI
    fun searchMovies(searchTerm: String) {
        if (searchTerm == lastSearchTerm && !movies.value.isNullOrEmpty()) {
            // If it's the same search term and data exists, don't re-fetch
            _isLoading.postValue(false)
            return
        }

        lastSearchTerm = searchTerm
        repository.saveLastSearchTerm(searchTerm)

        viewModelScope.launch {
            _isLoading.postValue(true) // Start loading

            try {
                if (searchTerm.isEmpty()) {
                    // If the search is cleared, show an empty list
                    repository.clearNonFavoriteMovies()  // Clear the Room database
                    _isLoading.postValue(false)
                } else {
                    // Fetch movies from API and save them into Room, clearing old results first
                    repository.fetchMoviesFromApi(searchTerm)
                }
            } catch (e: Exception) {
                _isLoading.postValue(false) // Handle error case
            } finally {
                _isLoading.postValue(false) // Stop loading
            }
        }
    }

    // Clear movies and search term
    fun clearMoviesAndSearchTerm() = viewModelScope.launch {
        repository.clearNonFavoriteMovies()
        lastSearchTerm = ""
        repository.saveLastSearchTerm("")  // Clear the saved search term
    }

    fun getMovieDetails(movieId: Long) = viewModelScope.launch {
        // Fetch the movie details from the repository
        val movie = repository.getMovieById(movieId) // Implement this in your repository
        _selectedMovie.postValue(movie!!)
    }

    fun toggleFavorite(movie: Movie) = viewModelScope.launch {
        movie.isFavorite = !movie.isFavorite
        Log.e(movie.trackName, movie.isFavorite.toString())
        repository.updateFavorite(movie)
    }
}
