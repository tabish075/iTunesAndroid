package com.droid.itunesandroid

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.droid.itunesandroid.data.repository.MovieRepository
import com.droid.itunesandroid.model.Movie
import com.droid.itunesandroid.ui.viewmodel.MovieViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@ExperimentalCoroutinesApi
class MovieViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: MovieRepository

    @InjectMocks
    private lateinit var viewModel: MovieViewModel

    @Mock
    private lateinit var moviesObserver: Observer<List<Movie>>

    @Mock
    private lateinit var loadingObserver: Observer<Boolean>

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        // Set the test dispatcher as Main dispatcher
        Dispatchers.setMain(testDispatcher)

        // Initialize the ViewModel
        viewModel = MovieViewModel(repository)

    }

    @Test
    fun testSearchMovies_success() = runTest {
        // Arrange
        val searchTerm = "star"
        val moviesList = listOf(
            Movie(1, "Star Wars", "url", 9.99, "USD", "Action", "-", "Great movie", false)
        )

        // Mock repository behavior for the suspend function
        `when`(repository.fetchMoviesFromApi(searchTerm)).thenReturn(moviesList)

        // Observe the LiveData
        viewModel.movies.observeForever(moviesObserver)
        viewModel.isLoading.observeForever(loadingObserver)

        // Act
        viewModel.searchMovies(searchTerm)

        // Assert
        verify(loadingObserver).onChanged(true)  // Loading started
        verify(moviesObserver).onChanged(moviesList)  // Movies returned
        verify(loadingObserver).onChanged(false)  // Loading stopped
    }


    @Test
    fun testToggleFavorite() = runTest {
        // Arrange
        val movie = Movie(1, "Star Wars", "url", 9.99, "USD", "Action", "-", "Great movie", false)

        // Act
        viewModel.toggleFavorite(movie)

        // Assert
        verify(repository).updateFavorite(movie)
        Assert.assertTrue(movie.isFavorite)
    }

    @Test
    fun testClearMoviesAndSearchTerm() = runTest {

        // Mock the behavior of getLastSearchTerm to return an empty string after clearing
        `when`(repository.getLastSearchTerm()).thenReturn("")

        // Act
        viewModel.clearMoviesAndSearchTerm()

        // Assert
        verify(repository).clearNonFavoriteMovies()
        verify(repository).saveLastSearchTerm("")
        Assert.assertEquals("", viewModel.getLastSearchTerm())
    }

    @After
    fun tearDown() {

        // Clean up observers to avoid leaks
        viewModel.movies.removeObserver(moviesObserver)
        viewModel.isLoading.removeObserver(loadingObserver)

        // Reset the Main dispatcher after the tests
        Dispatchers.resetMain()
    }
}
