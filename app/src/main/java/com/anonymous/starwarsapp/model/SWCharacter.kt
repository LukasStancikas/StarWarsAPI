package com.anonymous.starwarsapp.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SWCharacter(
    val name: String,
    val gender: String,
    val birthYear: String,
    val mass: String,
    val height: String,
    val skinColor: String,
    val hairColor: String,
    val url: String,
    val films: List<String>,
    val starships: List<String>
) : Parcelable {

    fun getFilmIds(): List<String>{
        return films.map {
            Uri.parse(it).lastPathSegment
        }
    }

    fun getStarshipIds(): List<String>{
        return starships.map {
            Uri.parse(it).lastPathSegment
        }
    }
}