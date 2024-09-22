import android.content.Context
import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.droid.itunesandroid.data.local.MovieDao
import com.droid.itunesandroid.data.repository.MovieRepository
import com.droid.itunesandroid.model.Movie
import com.droid.itunesandroid.model.MovieDto
import com.droid.itunesandroid.model.MovieResponse
import com.droid.itunesandroid.network.iTunesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class MovieRepositoryTest {

    // Rule for LiveData testing
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var movieDao: MovieDao

    @Mock
    private lateinit var iTunesApi: iTunesApi

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var sharedPreferences: SharedPreferences

    @Mock
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor

    private lateinit var repository: MovieRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        // Mock SharedPreferences and SharedPreferences.Editor behavior
        `when`(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences)
        `when`(sharedPreferences.edit()).thenReturn(sharedPreferencesEditor)
        `when`(sharedPreferencesEditor.putLong(anyString(), anyLong())).thenReturn(
            sharedPreferencesEditor
        )
        `when`(sharedPreferencesEditor.apply()).then {}

        // Initialize the repository with mocks
        repository = MovieRepository(movieDao, iTunesApi, context)
    }

    @Test
    fun testFetchMoviesFromApi() = runTest {
        // Arrange
        val searchTerm = "star"

        // Mock API response with a non-null list
        val movieDtoList = listOf(
            MovieDto(
                trackId = 1L,
                trackName = "Star Wars",
                artworkUrl100 = "url",
                trackPrice = 9.99,
                currency = "USD",
                primaryGenreName = "Action",
                shortDescription = "A great movie",
                longDescription = "A great movie set in space"
            )
        )

        val apiResponse = MovieResponse(
            resultCount = movieDtoList.size,
            results = movieDtoList  // Ensure this is non-null
        )

        // Mock the API to return the above response
        `when`(iTunesApi.searchMovies(searchTerm)).thenReturn(apiResponse)

        // Mock the database to return an empty list for existing movies (or customize as needed)
        `when`(movieDao.getAllMoviesList()).thenReturn(emptyList())

        // Act
        val result = repository.fetchMoviesFromApi(searchTerm)

        // Assert
        verify(movieDao).clearNonFavoriteMovies()
        verify(movieDao).insertMovies(anyList())
        Assert.assertEquals(1, result.size)
        Assert.assertEquals("Star Wars", result[0].trackName)
    }

    @Test
    fun testFetchMoviesFromApi_withFavorites() = runTest {
        // Arrange
        val searchTerm = "star"

        // Mock API response with a non-null list
        val movieDtoList = listOf(
            MovieDto(
                trackId = 1L,
                trackName = "Star Wars",
                artworkUrl100 = "url",
                trackPrice = 9.99,
                currency = "USD",
                primaryGenreName = "Action",
                shortDescription = "A great movie",
                longDescription = "A great movie set in space"
            )
        )

        val apiResponse = MovieResponse(
            resultCount = movieDtoList.size,
            results = movieDtoList  // Ensure this is non-null
        )

        // Mock the API to return the above response
        `when`(iTunesApi.searchMovies(searchTerm)).thenReturn(apiResponse)

        // Mock the database to return an existing list of favorite movies
        val existingMovies = listOf(
            Movie(
                trackId = 1L,
                trackName = "Star Wars",
                artworkUrl = "url",
                price = 9.99,
                currency = "USD",
                genre = "Action",
                shortDescription = "A great movie",
                longDescription = "A great movie set in space",
                isFavorite = true // This movie is marked as a favorite
            )
        )
        `when`(movieDao.getAllMoviesList()).thenReturn(existingMovies)

        // Act
        val result = repository.fetchMoviesFromApi(searchTerm)

        // Assert
        verify(movieDao).clearNonFavoriteMovies()
        verify(movieDao).insertMovies(anyList())
        Assert.assertEquals(1, result.size)
        Assert.assertEquals(
            true,
            result[0].isFavorite
        )  // Ensure the favorite status is retained
    }

    @After
    fun tearDown() {
        reset(movieDao, iTunesApi, sharedPreferences, sharedPreferencesEditor)
    }
}
