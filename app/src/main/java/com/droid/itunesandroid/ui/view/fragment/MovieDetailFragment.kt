package com.droid.itunesandroid.ui.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.droid.itunesandroid.R
import com.droid.itunesandroid.databinding.FragmentMovieDetailBinding
import com.droid.itunesandroid.ui.viewmodel.MovieViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MovieDetailFragment : Fragment() {

    private var _binding: FragmentMovieDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MovieViewModel by viewModels()
    private var movieId: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailBinding.inflate(inflater, container, false)

        // Set the ActionBar title
        (activity as AppCompatActivity).supportActionBar?.title = "Details"

        // Retrieve movieId from arguments using Safe Args
        movieId = MovieDetailFragmentArgs.fromBundle(requireArguments()).movieId

        // Load movie details
        loadMovieDetails(movieId)


        return binding.root
    }

    private fun loadMovieDetails(movieId: Long?) {
        // Assuming you have a method in your ViewModel to fetch movie details by ID
        // You should have a LiveData in your ViewModel that you can observe

        viewModel.getMovieDetails(movieId ?: return)

        viewModel.selectedMovie.observe(viewLifecycleOwner) { movie ->
            Log.e("MOVIID", movie!!.trackName)
            binding.textMovieTitle.text = movie.trackName
            binding.textMovieDescription.text = movie.longDescription

            // Generate HD (300x300) version of the artwork URL
            val artworkUrl300 = movie.artworkUrl.replace("100x100bb.jpg", "400x400bb.jpg")

            // Define the rounded corners transformation (specify corner radius in pixels)
            val requestOptions = RequestOptions().transform(RoundedCorners(20)) // 20 is the corner radius

            // Load the HD image with rounded corners using Glide, fallback to the original 100x100 image if HD fails
            Glide.with(binding.imageMovieArtwork.context)
                .load(artworkUrl300)
                .placeholder(R.drawable.placeholder)
                .apply(requestOptions) // Apply rounded corners
                .error(Glide.with(binding.imageMovieArtwork.context)
                    .load(movie.artworkUrl)
                    .apply(requestOptions)) // Apply rounded corners to the fallback as well
                .into(binding.imageMovieArtwork)


            binding.imageFavorite.setOnClickListener {
                movie.let {
                    viewModel.toggleFavorite(it)  // Toggle the favorite status
                    updateFavoriteIcon(it.isFavorite)  // Update the icon after toggle
                }
            }

            // Set favorite status (heart icon)
            updateFavoriteIcon(movie.isFavorite)
            binding.textMoviePrice.text = String.format(Locale.US, "%.2f %s", movie.price, movie.currency)
            binding.textMovieGenre.text = movie.genre
            binding.textMovieShortDescription.text = movie.shortDescription
        }
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        // Update the heart icon based on favorite status
        if (isFavorite) {
            binding.imageFavorite.setImageResource(R.drawable.baseline_favorite_24)  // Favorite icon
        } else {
            binding.imageFavorite.setImageResource(R.drawable.baseline_favorite_border_24)  // Non-favorite icon
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding reference to avoid memory leaks
    }
}
