package com.droid.itunesandroid.ui.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.droid.itunesandroid.R
import com.droid.itunesandroid.ui.adapter.MovieAdapter
import com.droid.itunesandroid.ui.viewmodel.MovieViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private val viewModel: MovieViewModel by viewModels()
    private lateinit var movieAdapter: MovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_favorites, container, false)

        // Set the ActionBar title
        (activity as AppCompatActivity).supportActionBar?.title = "Favorites"

        setupRecyclerView(view)

        // Observe favorite movies
        viewModel.favoriteMovies.observe(viewLifecycleOwner) { favorites ->
            movieAdapter.submitList(favorites)
        }

        return view
    }

    private fun setupRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        movieAdapter = MovieAdapter(
            onClick = { movie ->
                // Handle click on movie (optional navigation)
                val action = FavoritesFragmentDirections.actionFavoriteFragmentToMovieDetailFragment(movie.trackId)
                findNavController().navigate(action)
            },
            onFavoriteClick = { movie, _ ->
                // Handle favorite/unfavorite logic via ViewModel
                viewModel.toggleFavorite(movie)
            }
        )
        recyclerView.adapter = movieAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
}
