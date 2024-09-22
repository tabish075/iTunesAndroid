package com.droid.itunesandroid.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey val trackId: Long,
    val trackName: String,
    val artworkUrl: String,
    val price: Double,
    val currency: String,
    val genre: String,
    val shortDescription: String,
    val longDescription: String,
    var isFavorite: Boolean = false
)
