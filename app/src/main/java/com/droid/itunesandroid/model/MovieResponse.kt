package com.droid.itunesandroid.model

data class MovieResponse(
    val resultCount: Int,
    val results: List<MovieDto>
)

data class MovieDto(
    val trackId: Long,
    val trackName: String,
    val artworkUrl100: String,
    val trackPrice: Double,
    val currency: String,
    val primaryGenreName: String,
    val shortDescription: String?,
    val longDescription: String?
)
