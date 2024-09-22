package com.droid.itunesandroid.network

import com.droid.itunesandroid.model.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface iTunesApi {
    @GET("search")
    suspend fun searchMovies(
        @Query("term") searchTerm: String,
        @Query("country") country: String = "au",
        @Query("media") media: String = "movie"
    ): MovieResponse
}
