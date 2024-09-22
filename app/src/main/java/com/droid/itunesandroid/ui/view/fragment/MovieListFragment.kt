package com.droid.itunesandroid.ui.view.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.droid.itunesandroid.R
import com.droid.itunesandroid.databinding.FragmentMovieListBinding
import com.droid.itunesandroid.ui.adapter.MovieAdapter
import com.droid.itunesandroid.ui.viewmodel.MovieViewModel
import com.droid.itunesandroid.ui.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class MovieListFragment : Fragment() {

    private var _binding: FragmentMovieListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MovieViewModel by viewModels()
    private lateinit var movieAdapter: MovieAdapter
    private var searchHandler: Handler = Handler(Looper.getMainLooper()) // Handler for debouncing search
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieListBinding.inflate(inflater, container, false)

        displayLastVisitDate()
        setupRecyclerView()
        setupSearch()
        setupPullToRefresh()

        // Set the ActionBar title
        (activity as AppCompatActivity).supportActionBar?.title = "Browse"

        // Observe loading state to show or hide the refresh spinner
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefreshLayout.isRefreshing = isLoading
        }

        // Observe movies and update adapter
        viewModel.movies.observe(viewLifecycleOwner) { movies ->
            if (movies.isNullOrEmpty()) {
                showEmptyScreen()  // Show empty screen when no results
            } else {
                movieAdapter.submitList(movies)
                showMovieList() // Show movie list when there are results
            }
        }

        // Enable menu in the fragment
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_movie_list, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_favorites -> {
                // Navigate to FavoritesFragment
                findNavController().navigate(R.id.action_movieListFragment_to_favoritesFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        movieAdapter = MovieAdapter (
        { movie ->
            val action = MovieListFragmentDirections.actionMovieListFragmentToMovieDetailFragment(movie.trackId)
            findNavController().navigate(action)
        },
            onFavoriteClick = { movie, position ->
                // Handle favorite toggle via ViewModel
                viewModel.toggleFavorite(movie)
                movieAdapter.notifyItemChanged(position)
            }
        )
        binding.recyclerView.adapter = movieAdapter
    }

    private fun showClearBtn() {
        binding.btnClearSearch.visibility = View.VISIBLE
    }
    private fun hideClearBtn() {
        binding.btnClearSearch.visibility = View.GONE
    }

    private fun setupSearch() {

        var isSearchRestored = true

        // Restore the last search term without triggering the search
        val lastSearchTerm = viewModel.getLastSearchTerm()
        if (!lastSearchTerm.isNullOrEmpty()) {
            binding.editTextSearch.setText(lastSearchTerm)  // Set the search term programmatically
            showClearBtn()
        } else {
            hideClearBtn()
        }

        // Add a clear (X) button to clear the search term and movies
        binding.btnClearSearch.setOnClickListener {
            binding.editTextSearch.text.clear()
            viewModel.clearMoviesAndSearchTerm()  // Clear Room and search term
        }

        // Set up the TextWatcher to handle user input
        val textWatcher = object : TextWatcher {
            private var searchRunnable: Runnable = Runnable {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isSearchRestored) {
                    isSearchRestored = false  // Reset the flag after the search term is restored
                    return  // Skip API call when search term is programmatically restored
                }

                // Cancel any previously queued search runnable
                searchHandler.removeCallbacks(searchRunnable)

                searchRunnable = Runnable {
                    val searchTerm = s.toString().trim()
                    if (searchTerm.isNotEmpty()) {
                        showClearBtn()
                        viewModel.searchMovies(searchTerm)  // Trigger search from API
                    } else {
                        hideClearBtn()
                        movieAdapter.submitList(emptyList())  // Clear the movie list when search is empty
                        showEmptyScreen()
                    }
                }

                // Post the search runnable with a delay (debounce to prevent too many requests)
                searchHandler.postDelayed(searchRunnable, 500)
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.editTextSearch.addTextChangedListener(textWatcher)
    }

    private fun setupPullToRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            val searchTerm = binding.editTextSearch.text.toString().trim()
            if (searchTerm.isNotEmpty()) {
                // Trigger refresh from the API for search term
                viewModel.searchMovies(searchTerm)
            } else {
                movieAdapter.submitList(emptyList())  // Clear the movie list when search is empty
                showEmptyScreen()
            }
        }
    }

    private fun showEmptyScreen() {
        binding.swipeRefreshLayout.visibility = View.GONE
        binding.emptyView.visibility = View.VISIBLE  // Display an empty view
    }

    private fun showMovieList() {
        binding.swipeRefreshLayout.visibility = View.VISIBLE
        binding.emptyView.visibility = View.GONE  // Hide the empty view when there are results
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // Clear binding reference to avoid memory leaks
        searchHandler.removeCallbacksAndMessages(null)  // Clean up the handler to avoid memory leaks
    }

    override fun onResume() {
        super.onResume()
        sharedViewModel.saveLastScreen("MovieListFragment")
    }

    override fun onPause() {
        super.onPause()
        sharedViewModel.saveLastVisitDate()
    }

    private fun displayLastVisitDate() {
        val lastVisitDateMillis = sharedViewModel.getLastVisitDate()
        if (lastVisitDateMillis > 0) {
            val lastVisitDate = Date(lastVisitDateMillis)
            val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            binding.textLastVisit.text = "Last Visit: ${dateFormat.format(lastVisitDate)}"
        } else {
            binding.textLastVisit.text = "Welcome! This is your first visit."
        }
    }
}
