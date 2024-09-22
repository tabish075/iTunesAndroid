package com.droid.itunesandroid

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.droid.itunesandroid.util.SharedPreferencesHelper
import com.droid.itunesandroid.ui.viewmodel.SharedViewModel
import org.junit.*
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class SharedViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var sharedPrefsHelper: SharedPreferencesHelper

    private lateinit var sharedViewModel: SharedViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        // Initialize ViewModel with the mocked SharedPreferencesHelper
        sharedViewModel = SharedViewModel(sharedPrefsHelper)
    }

    @Test
    fun testSaveLastVisitDate() {
        // Act
        sharedViewModel.saveLastVisitDate()

        // Assert: Verify that the SharedPreferencesHelper method is called
        verify(sharedPrefsHelper).saveLastVisitDate()
    }

    @Test
    fun testGetLastVisitDate() {
        // Arrange: Set a predefined value to be returned by the helper
        val expectedDate = 1620000000000L
        `when`(sharedPrefsHelper.getLastVisitDate()).thenReturn(expectedDate)

        // Act
        val lastVisitDate = sharedViewModel.getLastVisitDate()

        // Assert: Verify that the value returned by the ViewModel is correct
        Assert.assertEquals(expectedDate, lastVisitDate)
    }

    @Test
    fun testSaveLastScreen() {
        // Arrange: Set the screen name to be saved
        val screenName = "MovieDetailScreen"

        // Act
        sharedViewModel.saveLastScreen(screenName)

        // Assert: Verify that the SharedPreferencesHelper method is called with the correct argument
        verify(sharedPrefsHelper).saveLastScreen(screenName)
    }

    @Test
    fun testGetLastScreen() {
        // Arrange: Set a predefined screen name to be returned by the helper
        val expectedScreen = "HomeScreen"
        `when`(sharedPrefsHelper.getLastScreen()).thenReturn(expectedScreen)

        // Act
        val lastScreen = sharedViewModel.getLastScreen()

        // Assert: Verify that the value returned by the ViewModel is correct
        Assert.assertEquals(expectedScreen, lastScreen)
    }

    @After
    fun tearDown() {
        // Reset the mock after each test to avoid interference between tests
        reset(sharedPrefsHelper)
    }
}
