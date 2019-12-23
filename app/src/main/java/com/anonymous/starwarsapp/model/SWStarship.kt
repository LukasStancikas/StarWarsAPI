package com.anonymous.starwarsapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SWStarship(
    val name: String,
    val model: String,
    val manufacturer: String,
    val costInCredits: String,
    val hyperdriveRating: String,
    val starshipClass: String,
    val pilots: List<String>
) : Parcelable