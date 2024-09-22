package com.droid.itunesandroid.ui.adapter

import android.net.LocalSocketAddress
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.droid.itunesandroid.R
import com.droid.itunesandroid.databinding.ItemMovieBinding
import com.droid.itunesandroid.model.Movie
import java.util.Locale

class MovieAdapter(
    private val onClick: (Movie) -> Unit,
    private val onFavoriteClick: (Movie, Int) -> Unit
) : ListAdapter<Movie, MovieAdapter.MovieViewHolder>(MovieDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = getItem(position)
        holder.bind(movie)
        holder.itemView.setOnClickListener { onClick(movie) }
        // Handle favorite click
        holder.binding.imageFavorite.setOnClickListener { onFavoriteClick(movie, position) }
    }

    class MovieViewHolder(val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {
            binding.textMovieTitle.text = movie.trackName
            // Generate HD (300x300) version of the artwork URL
            val artworkUrl300 = movie.artworkUrl.replace("100x100bb.jpg", "300x300bb.jpg")

            // Load the HD image using Glide, fallback to the original 100x100 image if the HD image fails
            Glide.with(binding.imageMovieArtwork.context)
                .load(artworkUrl300) // Load the HD image
                .placeholder(R.drawable.placeholder)
                .error(
                    Glide.with(binding.imageMovieArtwork.context)
                        .load(movie.artworkUrl)
                ) // Fallback to the original image if the HD fails
                .into(binding.imageMovieArtwork)

            // Set the favorite icon state based on the movie's isFavorite status
            binding.imageFavorite.setImageResource(
                if (movie.isFavorite) R.drawable.baseline_favorite_24 else R.drawable.baseline_favorite_border_24
            )
            binding.textMoviePrice.text = String.format(Locale.US, "%.2f %s", movie.price, movie.currency)
            binding.textMovieGenre.text = movie.genre
            binding.textMovieShortDescription.text = movie.shortDescription
        }
    }

    class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.trackId == newItem.trackId
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            // Compare all properties, including isFavorite, to detect changes
            return oldItem.trackName == newItem.trackName &&
                    oldItem.artworkUrl == newItem.artworkUrl &&
                    oldItem.price == newItem.price &&
                    oldItem.genre == newItem.genre &&
                    oldItem.isFavorite == newItem.isFavorite  // Make sure this is included
        }
    }
}
