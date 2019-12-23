package com.anonymous.starwarsapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SWFilm(
    val title: String,
    val episodeId: String,
    val openingCrawl: String,
    val director: String,
    val producer: String,
    val releaseDate: String,
    val characters: List<String>
) : Parcelable